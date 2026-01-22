package com.emmanuel.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceImplTest {

  @Mock UserRepository userRepository;

  @Mock UserMapper userMapper;

  @Mock PasswordEncoder passwordEncoder;

  @Mock AuthenticationManager authManager;

  @Mock JwtTokenUtil jwtTokenUtil;

  @Mock StorageService storageService;

  @InjectMocks private AuthServiceImpl authService;

  private AutoCloseable openMocks;

  private SignUpRequest request;
  private User mappedUser;
  private User savedUser;

  @BeforeEach
  void setUp() throws IOException {
    openMocks = org.mockito.MockitoAnnotations.openMocks(this);

    request =
        new SignUpRequest(
            "testuser", "test@email.com", "Password123!", "testuser", "testuser", null);

    mappedUser = new User();
    mappedUser.setUsername("testuser");
    mappedUser.setEmail("test@email.com");
    mappedUser.setPassword("Password123!");
    mappedUser.setFirstName("testuser");
    mappedUser.setLastName("testuser");

    savedUser = new User();
    savedUser.setId(1L);
    savedUser.setUsername("testuser");
    savedUser.setEmail("test@email.com");
    savedUser.setPassword("EncodedPassword123!");
    savedUser.setFirstName("testuser");
    savedUser.setLastName("testuser");
    savedUser.setRoles(Set.of(Role.ROLE_USER));
    savedUser.setPermissions(Set.of(Permission.USER_READ));
    savedUser.setCreatedAt(Instant.now());
    savedUser.setUpdatedAt(Instant.now());
    savedUser.setVersion(0L);

    when(storageService.generateAndUploadAvatar(anyString(), anyString()))
        .thenReturn("http://fake-avatar.png");

    when(userMapper.toEntity(request)).thenReturn(mappedUser);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void createUser_whenValidRequest_ShouldSignUp() throws IOException {
    when(userRepository.findByUsername(request.username())).thenReturn(java.util.Optional.empty());
    when(userRepository.findByEmail(request.email())).thenReturn(java.util.Optional.empty());
    when(userMapper.toEntity(request)).thenReturn(mappedUser);
    when(passwordEncoder.encode(request.password())).thenReturn("EncodedPassword123!");
    when(userRepository.save(mappedUser)).thenReturn(savedUser);
    when(userMapper.toDto(savedUser))
        .thenReturn(
            new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt(),
                savedUser.getLogoUrl()));

    UserResponse response = authService.signUp(request);

    assertNotNull(response);
    assertEquals(savedUser.getId(), response.id());
    assertEquals(savedUser.getUsername(), response.username());
    assertEquals(savedUser.getEmail(), response.email());
    assertNotNull(response.createdAt());
    assertNotNull(response.updatedAt());
    assertEquals(0L, savedUser.getVersion());
    assertTrue(savedUser.isEnabled());
    assertTrue(savedUser.isAccountNonExpired());
    assertTrue(savedUser.isAccountNonLocked());
    assertFalse(savedUser.isDeleted());
    assertNull(savedUser.getLastActivityAt());
    assertNull(savedUser.getLastLoginAt());
    assertEquals(Set.of(Role.ROLE_USER), savedUser.getRoles());
  }


  @Test
  void login_whenCredentialsAreValid_shouldReturnTokens() {
    LoginRequest loginRequest = new LoginRequest("testuser", "Password123!");

    Authentication authentication = mock(Authentication.class);
    when(authManager.authenticate(any())).thenReturn(authentication);
    when(userRepository.findByUsername(loginRequest.username()))
        .thenReturn(java.util.Optional.of(savedUser));

    when(jwtTokenUtil.generateToken(eq(savedUser.getUsername()), anySet(), anySet()))
        .thenReturn("accessToken");

    when(jwtTokenUtil.generateRefreshToken(savedUser.getUsername())).thenReturn("refreshToken");

    savedUser.setRefreshTokens(new HashSet<>());
    savedUser.setVersion(1L);
    savedUser.setUpdatedBy(loginRequest.username());
    savedUser.setLoginCount(1);
    savedUser.setLastLoginAt(Instant.now());
    savedUser.setLastActivityAt(Instant.now());

    JwtResponse jwtResponse = authService.login(loginRequest);

    assertNotNull(jwtResponse);
    assertEquals("accessToken", jwtResponse.accessToken());
    assertEquals("refreshToken", jwtResponse.refreshToken());
    assertTrue(savedUser.getRefreshTokens().contains("refreshToken"));
    assertEquals(1, savedUser.getVersion());
    assertEquals(loginRequest.username(), savedUser.getUpdatedBy());
    assertEquals(1, savedUser.getLoginCount());
    assertNotNull(savedUser.getLastLoginAt());
    assertNotNull(savedUser.getLastActivityAt());
  }

  @Test
  void refreshToken_whenValidToken_shouldReturnNewAccessToken() {
    String existingRefreshToken = "refreshToken";

    savedUser.setRefreshTokens(new HashSet<>());
    savedUser.getRefreshTokens().add(existingRefreshToken);
    when(jwtTokenUtil.validateRefreshToken(existingRefreshToken)).thenReturn(true);
    when(jwtTokenUtil.getUsernameFromToken(existingRefreshToken))
        .thenReturn(savedUser.getUsername());
    when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(savedUser));
    when(jwtTokenUtil.generateToken(eq("testuser"), anySet(), anySet()))
        .thenReturn("newAccessToken");
  }

  @Test
  void refreshToken_whenInvalidToken_shouldThrowException() {
    String invalidRefreshToken = "invalidRefreshToken";
    TokenRefreshRequest refreshRequest = new TokenRefreshRequest(invalidRefreshToken);
    when(jwtTokenUtil.validateRefreshToken(invalidRefreshToken)).thenReturn(false);

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authService.refreshToken(refreshRequest));

    String expectedMessage = "Invalid refresh token";
    String actualMessage = exception.getReason();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void refreshToken_whenTokenNotInUserTokenList_shouldThrowException() {
    String unrecognizedRefreshToken = "unrecognizedRefreshToken";
    TokenRefreshRequest refreshRequest = new TokenRefreshRequest(unrecognizedRefreshToken);
    when(jwtTokenUtil.validateRefreshToken(unrecognizedRefreshToken)).thenReturn(true);
    when(jwtTokenUtil.getUsernameFromToken(unrecognizedRefreshToken))
        .thenReturn(savedUser.getUsername());
    when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(savedUser));

    savedUser.setRefreshTokens(new HashSet<>());
    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> authService.refreshToken(refreshRequest));

    String expectedMessage = "Refresh token not recognized";
    String actualMessage = exception.getReason();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}
