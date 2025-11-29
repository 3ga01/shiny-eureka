package com.emmanuel.user_service.unit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.emmanuel.user_service.controller.AuthController;
import com.emmanuel.user_service.dto.*;
import com.emmanuel.user_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthControllerUnitTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    public AuthControllerUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_shouldCallAuthServiceAndReturnResponse() {
        SignUpRequest request = new SignUpRequest("user", "user@mail.com", "Password1!", "First", "Last");
        CreateUserResponse expectedResponse = new CreateUserResponse(1L, "user", "user@mail.com", null, null);

        when(authService.createUser(request)).thenReturn(expectedResponse);

        CreateUserResponse response = authController.signup(request);

        assertEquals(expectedResponse, response);
        verify(authService, times(1)).createUser(request);
    }

    @Test
    void signup_shouldReturnBadRequestForInvalidEmail() throws Exception {
        SignUpRequest request = new SignUpRequest(
                "user",
                "invalid-email", // invalid email
                "Password1!",
                "First",
                "Last"
        );

    }

    @Test
    void login_shouldCallAuthServiceAndReturnJwtResponse() {
        LoginRequest request = new LoginRequest("user", "Password1!");
        JwtResponse expectedResponse = new JwtResponse("access", "refresh");

        when(authService.login(request)).thenReturn(expectedResponse);

        JwtResponse response = authController.login(request);

        assertEquals(expectedResponse, response);
        verify(authService, times(1)).login(request);
    }

    @Test
    void refresh_shouldCallAuthServiceAndReturnJwtResponse() {
        TokenRefreshRequest request = new TokenRefreshRequest("refreshToken");
        JwtResponse expectedResponse = new JwtResponse("access", "refresh");

        when(authService.refreshToken(request)).thenReturn(expectedResponse);

        JwtResponse response = authController.refresh(request);

        assertEquals(expectedResponse, response);
        verify(authService, times(1)).refreshToken(request);
    }

}