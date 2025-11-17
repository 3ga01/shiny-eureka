package com.emmanuel.user_service.security;

import com.emmanuel.user_service.mapper.UserMapper;
import io.jsonwebtoken.*;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

  private final long jwtExpirationMs = 24 * 60 * 60 * 1000;

  private final PrivateKey privateKey;
  private final PublicKey publicKey;
    private final UserMapper userMapper;

    // Load file paths from application.properties
  public JwtTokenUtil(
          @Value("${jwt.private-key-path}") String privateKeyPath,
          @Value("${jwt.public-key-path}") String publicKeyPath, UserMapper userMapper)
      throws Exception {
    this.privateKey = loadPrivateKey(privateKeyPath);
    this.publicKey = loadPublicKey(publicKeyPath);
      this.userMapper = userMapper;
  }

  private PrivateKey loadPrivateKey(String resourcePath) throws Exception {
    try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
      String key =
          new String(is.readAllBytes())
              .replaceAll("-----BEGIN PRIVATE KEY-----", "")
              .replaceAll("-----END PRIVATE KEY-----", "")
              .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(key);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
      return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
  }

  private PublicKey loadPublicKey(String resourcePath) throws Exception {
    try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
      String key =
          new String(is.readAllBytes())
              .replaceAll("-----BEGIN PUBLIC KEY-----", "")
              .replaceAll("-----END PUBLIC KEY-----", "")
              .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(key);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
      return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
  }

  public String generateToken(String username, Set<String> roles, Set<String> permissions) {
    return Jwts.builder()
        .setSubject(username)
        .claim("roles", roles)
            .claim("permissions", permissions)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(privateKey, SignatureAlgorithm.RS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException ex) {
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public Set<String> getRolesFromToken(String token) {
    return ((java.util.List<?>)
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles"))
        .stream().map(Object::toString).collect(Collectors.toSet());
  }

  public String generateRefreshToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(privateKey, SignatureAlgorithm.RS256)
        .compact();
  }

  public boolean validateRefreshToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
      return true;
    } catch (JwtException ex) {
      return false;
    }
  }
}
