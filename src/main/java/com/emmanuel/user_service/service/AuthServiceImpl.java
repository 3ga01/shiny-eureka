package com.emmanuel.user_service.service;

import com.emmanuel.user_service.dto.*;
import com.emmanuel.user_service.exception.DuplicateResourceException;
import com.emmanuel.user_service.exception.security.AuthenticationFailedException;
import com.emmanuel.user_service.mapper.UserMapper;
import com.emmanuel.user_service.model.Permission;
import com.emmanuel.user_service.model.Role;
import com.emmanuel.user_service.model.User;
import com.emmanuel.user_service.repository.UserRepository;
import com.emmanuel.user_service.security.JwtTokenUtil;
import com.emmanuel.user_service.utility.ErrorMessages;
import io.sentry.Sentry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

  @Override
  public CreateUserResponse createUser(SignUpRequest signUpRequest) {

    if (userRepository.findByUsername(signUpRequest.username()).isPresent()) {
      throw new DuplicateResourceException(ErrorMessages.USERNAME_ALREADY_EXISTS);
    }

    if (userRepository.findByEmail(signUpRequest.email()).isPresent()) {
      throw new DuplicateResourceException(ErrorMessages.EMAIL_ALREADY_EXISTS);
    }

    User user = userMapper.toEntity(signUpRequest);
    user.setPassword(passwordEncoder.encode(signUpRequest.password()));

    if (user.getRoles() == null || user.getRoles().isEmpty()) {
      user.setRoles(Set.of(Role.ROLE_USER));
    }

    if (user.getPermissions() == null || user.getPermissions().isEmpty()) {
      user.setPermissions(Set.of(Permission.USER_READ));
    }

    User saved = userRepository.save(user);

    return userMapper.toDto(saved);
  }

  @Override
  @Transactional
  public JwtResponse login(LoginRequest loginRequest) {

    try {
      authManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.username(), loginRequest.password()));
    } catch (BadCredentialsException | UsernameNotFoundException ex) {
      Sentry.captureException(ex);
      throw new AuthenticationFailedException("Invalid username or password");
    }

    var user =
        userRepository
            .findByUsernameForUpdate(loginRequest.username())
            .orElseThrow(
                () -> new AuthenticationFailedException("User not found after authentication"));

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
    User user = userRepository.findByUsernameForUpdate(username).orElseThrow();

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
