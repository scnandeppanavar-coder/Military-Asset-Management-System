package com.mams.service;

import com.mams.dto.LoginRequest;
import com.mams.dto.LoginResponse;
import com.mams.entity.Base;
import com.mams.entity.Role;
import com.mams.entity.User;
import com.mams.repository.BaseRepository;
import com.mams.repository.UserRepository;
import com.mams.security.CustomUserDetails;
import com.mams.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BaseRepository baseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User adminUser;
    private Base alphaBase;

    @BeforeEach
    void setUp() {
        alphaBase = Base.builder()
                .id(1L)
                .baseCode("BASE-ALPHA")
                .baseName("Fort Alpha")
                .status("ACTIVE")
                .build();

        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .password("$2a$10$encodedPassword")
                .fullName("General Vance")
                .email("admin@mams.mil")
                .role(Role.ADMIN)
                .base(null)
                .build();
    }

    @Test
    @DisplayName("Should successfully login and return JWT token")
    void testSuccessfulLogin() {
        LoginRequest request = new LoginRequest("admin", "Admin@123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("admin", "Admin@123"));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("mock-jwt-token-xyz");

        LoginResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        assertEquals("mock-jwt-token-xyz", response.getToken());
        verify(auditLogService).log(eq(adminUser), eq("LOGIN"), eq("USER"), eq(1L), anyString());
    }

    @Test
    @DisplayName("Should throw exception when login credentials are invalid")
    void testFailedLoginBadCredentials() {
        LoginRequest request = new LoginRequest("admin", "WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
        verify(jwtService, never()).generateToken(any(CustomUserDetails.class));
    }
}
