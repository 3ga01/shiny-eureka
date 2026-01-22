package com.emmanuel.user_service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmanuel.user_service.dto.request.LoginRequest;
import com.emmanuel.user_service.dto.request.SignUpRequest;
import com.emmanuel.user_service.repository.UserRepository;
import com.emmanuel.user_service.service.storage.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired private WebApplicationContext context;

  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @MockitoBean private StorageService storageService;

  @BeforeEach
  void setup() throws IOException {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    when(storageService.generateAndUploadAvatar(anyString(), anyString()))
        .thenReturn("http://fake-avatar.png");
  }

  @AfterEach
  void cleanup() {
    userRepository.deleteAll();
  }

  @Test
  void signup_shouldReturnCreatedUser() throws Exception {

    SignUpRequest request =
        new SignUpRequest(
            "testuser", "test@example.com", "Password1!", "John", "Doe", "logoUrl.png");

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  void login_shouldReturnJwtToken() throws Exception {

    // First, create the user
    SignUpRequest signupRequest =
        new SignUpRequest("loginuser", "login@example.com", "Password1!", "Alice", "Smith", null);

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
        .andExpect(status().isCreated());

    // Then, try login
    LoginRequest loginRequest = new LoginRequest("loginuser", "Password1!");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }
}
