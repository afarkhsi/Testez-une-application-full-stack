package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerUnitTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        authController = new AuthController(
                authenticationManager,
                passwordEncoder,
                jwtUtils,
                userRepository
        );
    }

    @Test
    @DisplayName("User authentication > return JWT response")
    void userAuthenticated_returnJwtResponse(){
        LoginRequest loginRequest = new LoginRequest();
        String email = "currentUser@gmail.com";
        String password = "currentUser";

        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String token = "token";

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(4L)
                .username(email)
                .lastName("current")
                .firstName("user")
                .admin(false)
                .password(password)
                .build();

        User user = new User();
        user.setAdmin(false);
        boolean isAdmin = user.isAdmin();

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password))).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        securityContext.setAuthentication(authentication);

        when(jwtUtils.generateJwtToken(authentication)).thenReturn(token);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

        JwtResponse jwtResponse = new JwtResponse(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                isAdmin
        );

        ResponseEntity<?> authenticateUser = authController.authenticateUser(loginRequest);
        ResponseEntity<?> responseEntityOK = ResponseEntity.ok(jwtResponse);

        assertEquals(authenticateUser.getStatusCode(),responseEntityOK.getStatusCode());
        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByEmail(userDetails.getUsername());
    }

    @Test
    @DisplayName("bad credentials on authentication")
    void badCredentials_authenticationDenied(){
        LoginRequest loginRequest = new LoginRequest();
        String email = "wrong@email.com";
        String password = "wrong";

        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String token = "error_token";
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username(email)
                .password(password)
                .build();

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password))).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        securityContext.setAuthentication(authentication);

        when(jwtUtils.generateJwtToken(authentication)).thenReturn(token);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

        authController.authenticateUser(loginRequest);

        assertEquals(user, null);
        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(2)).findByEmail(email);
    }

    @Test
    @DisplayName("test new user registration with registerUser > response ok")
    void whenValidSignUpRequest_thenReturnResponseEntityOK(){
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new@users.com");
        signupRequest.setFirstName("new");
        signupRequest.setLastName("user");
        signupRequest.setPassword("password");

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPassword(signupRequest.getPassword());
        user.setAdmin(false);

        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(user.getPassword());
        when(userRepository.save(user)).thenReturn(user);

        MessageResponse messageResponse = new MessageResponse("User registered successfully!");
        ResponseEntity<?> responseEntity = ResponseEntity.ok(messageResponse);
        ResponseEntity<?> registerUser = authController.registerUser(signupRequest);
        assertEquals(registerUser.getStatusCode(), responseEntity.getStatusCode());
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("test new user registration with registerUser > bad request")
    void whenEmailAlreadyExists_thenReturnBadRequest(){
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@email.com");
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        MessageResponse messageResponse = new MessageResponse("Error: Email is already taken!");
        ResponseEntity<?> responseEntityBadRequest = ResponseEntity.badRequest().body(messageResponse);
        ResponseEntity<?> registerUser = authController.registerUser(signupRequest);

        assertEquals(registerUser.getStatusCode(), responseEntityBadRequest.getStatusCode());
        verify(userRepository, times(1)).existsByEmail(signupRequest.getEmail());
    }
}