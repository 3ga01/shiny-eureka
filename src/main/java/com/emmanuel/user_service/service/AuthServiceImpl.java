package com.emmanuel.user_service.service;

import com.emmanuel.user_service.dto.request.LoginRequest;
import com.emmanuel.user_service.dto.request.SignUpRequest;
import com.emmanuel.user_service.dto.request.TokenRefreshRequest;
import com.emmanuel.user_service.dto.response.JwtResponse;
import com.emmanuel.user_service.dto.response.UserResponse;
import com.emmanuel.user_service.mapper.UserMapper;
import com.emmanuel.user_service.model.Permission;
import com.emmanuel.user_service.model.Role;
import com.emmanuel.user_service.model.User;
import com.emmanuel.user_service.repository.UserRepository;
import com.emmanuel.user_service.security.JwtTokenUtil;
import com.emmanuel.user_service.service.storage.StorageService;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

  @Override
  @Transactional
  public UserResponse signUp(SignUpRequest signUpRequest) throws IOException {

    User user = userMapper.toEntity(signUpRequest);
    user.setPassword(passwordEncoder.encode(signUpRequest.password()));

    user.setRoles(Set.of(Role.ROLE_USER));

    user.setPermissions(Set.of(Permission.USER_READ));

    if (signUpRequest.logoUrl() == null || signUpRequest.logoUrl().isEmpty()) {
      String avatarUrl =
          storageService.generateAndUploadAvatar(
              signUpRequest.firstName(), signUpRequest.lastName());
      user.setLogoUrl(avatarUrl);
    } else {
      user.setLogoUrl(signUpRequest.logoUrl());
    }

    User saved = userRepository.save(user);

    return userMapper.toDto(saved);
  }

  @Override
  @Transactional
  public JwtResponse login(LoginRequest loginRequest) {

    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

    User user = userRepository.findByUsername(loginRequest.username()).orElseThrow();

    String accessToken =
        jwtTokenUtil.generateToken(
            user.getUsername(),
            user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
            user.getPermissions().stream().map(Enum::name).collect(Collectors.toSet()));
    String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername());

    // Save refresh token to user
    user.getRefreshTokens().add(refreshToken);
    userRepository.save(user);

    return new JwtResponse(accessToken, refreshToken);
  }

  @Override
  @Transactional
  public JwtResponse refreshToken(TokenRefreshRequest refreshToken) {
    String requestToken = refreshToken.refreshToken();

    if (!jwtTokenUtil.validateRefreshToken(requestToken)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token");
    }

    String username = jwtTokenUtil.getUsernameFromToken(requestToken);
    User user = userRepository.findByUsername(username).orElseThrow();

    if (!user.getRefreshTokens().contains(requestToken)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token not recognized");
    }

    String accessToken =
        jwtTokenUtil.generateToken(
            username,
            user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
            user.getPermissions().stream().map(Enum::name).collect(Collectors.toSet()));
    return new JwtResponse(accessToken, requestToken);
  }
}
