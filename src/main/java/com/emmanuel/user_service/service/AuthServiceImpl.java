package com.emmanuel.user_service.service;

import com.emmanuel.user_service.dto.request.*;
import com.emmanuel.user_service.dto.response.JwtResponse;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.dto.util.JwtTokens;
import com.emmanuel.user_service.mapper.UserMapper;
import com.emmanuel.user_service.model.user.Permission;
import com.emmanuel.user_service.model.user.Role;
import com.emmanuel.user_service.model.user.User;
import com.emmanuel.user_service.model.user.VerificationToken;
import com.emmanuel.user_service.repository.UserRepository;
import com.emmanuel.user_service.repository.VerificationTokenRepository;
import com.emmanuel.user_service.security.JwtTokenUtil;
import com.emmanuel.user_service.service.mail.MailService;
import com.emmanuel.user_service.service.storage.StorageService;
import com.emmanuel.user_service.service.verification.VerificationService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserMapper userMapper;
  private final StorageService storageService;
  private final MailService mailService;
  private final VerificationService verificationService;
  private final VerificationTokenRepository verificationTokenRepository;

  @Value("${app.frontend-url}")
  private String frontendBaseUrl;

  @Override
  @Transactional
  public UserResponse signUp(SignUpRequest signUpRequest) throws IOException, MessagingException {

    User user = createUser(signUpRequest);

    User savedUser = userRepository.save(user);

    String token = createVerification(savedUser);

    String activationLink = frontendBaseUrl + "/verify?token=" + token;

    sendVerificationEmail(savedUser, activationLink);

    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional
  public JwtResponse login(LoginRequest loginRequest) {

    authenticate(loginRequest);

    User user = loadUser(loginRequest.username());

    JwtTokens tokens = generateTokens(user);

    storeRefreshToken(user, tokens.refreshToken());

    return new JwtResponse(tokens.accessToken(), tokens.refreshToken());
  }

  @Override
  @Transactional
  public JwtResponse refreshToken(TokenRefreshRequest request) {

    String refreshToken = request.refreshToken();

    validateRefreshToken(refreshToken);

    String username = extractUsername(refreshToken);

    User user = loadUser(username);

    assertTokenBelongsToUser(user, refreshToken);

    String accessToken = generateAccessToken(user);

    return new JwtResponse(accessToken, refreshToken);
  }

  @Override
  public UserResponse enableUserAccount(String token) {
    return null;
  }

  @Override
  public UserResponse activateUserAccount(String token) {
    VerificationToken verificationToken =
        verificationTokenRepository.findByToken(token).orElseThrow();
    if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
      return null;
    }
    User user = verificationToken.getUser();
    user.setEnabled(true);
    userRepository.save(user);
    return userMapper.toDto(user);
  }

  private MailRequest buildVerificationEmailRequest(User user, String activationLink) {
    return MailRequest.builder()
        .to(user.getEmail())
        .subject("Welcome to Our Service")
        .htmlFilePath("mail-templates/welcome.html")
        .activationUrl(activationLink)
        .user(user)
        .build();
  }

  private User createUser(SignUpRequest request) throws IOException {
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRoles(Set.of(Role.ROLE_USER));
    user.setPermissions(Set.of(Permission.USER_READ));
    user.setLogoUrl(resolveLogoUrl(request));
    return user;
  }

  private String resolveLogoUrl(SignUpRequest request) throws IOException {
    if (request.logoUrl() == null || request.logoUrl().isEmpty()) {
      return storageService.generateAndUploadAvatar(request.firstName(), request.lastName());
    }
    return request.logoUrl();
  }

  private String createVerification(User user) {
    String token = verificationService.generateToken();

    Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);

    VerificationRequest request =
        VerificationRequest.builder().user(user).token(token).expiryDate(expiryDate).build();

    verificationService.saveVerificationToken(request);

    return token;
  }

  private void sendVerificationEmail(User user, String activationLink)
      throws MessagingException, IOException {
    mailService.sendHtmlEmailFromFile(buildVerificationEmailRequest(user, activationLink));
  }

  private void authenticate(LoginRequest request) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
  }

  private User loadUser(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  private JwtTokens generateTokens(User user) {
    String accessToken = generateAccessToken(user);

    String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername());

    return new JwtTokens(accessToken, refreshToken);
  }

  private void storeRefreshToken(User user, String refreshToken) {
    user.getRefreshTokens().add(refreshToken);
    userRepository.save(user);
  }

  private void validateRefreshToken(String token) {
    if (!jwtTokenUtil.validateRefreshToken(token)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
    }
  }

  private String extractUsername(String refreshToken) {
    return jwtTokenUtil.getUsernameFromToken(refreshToken);
  }

  private void assertTokenBelongsToUser(User user, String refreshToken) {
    if (!user.getRefreshTokens().contains(refreshToken)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
    }
  }

  private String generateAccessToken(User user) {
    return jwtTokenUtil.generateToken(
        user.getUsername(),
        user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
        user.getPermissions().stream().map(Enum::name).collect(Collectors.toSet()));
  }
}
