package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;

public class JwtUtilsUnitTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JwtUtils();
        // Accès aux propriétés privées avec test context
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3_600_000); // 10 minutes
    }

    @Test
    public void testGenerateAndValidateJwtToken() {
        // Arrange
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("test@example.com", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    public void testValidateJwtToken_withSignatureException() {
        String invalidToken = "invalid.token.signature";

        JwtUtils spyJwt = spy(jwtUtils);
        doThrow(new SignatureException("Invalid signature")).when(spyJwt).validateJwtToken(invalidToken);

        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    public void testValidateJwtToken_withMalformedJwtException() {
        String malformedToken = "this.is.not.jwt";

        // Will log but return false
        assertFalse(jwtUtils.validateJwtToken(malformedToken));
    }

    @Test
    public void testValidateJwtToken_withExpiredJwtException() {
        String expiredToken = Jwts.builder()
                .setSubject("expired@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000)) // déjà expiré
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    public void testValidateJwtToken_withUnsupportedJwtException() {
        String unsupportedToken = Jwts.builder()
                .setPayload("{\"sub\":\"test\"}")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(unsupportedToken));
    }

    @Test
    public void testValidateJwtToken_withIllegalArgumentException() {
        assertFalse(jwtUtils.validateJwtToken(""));
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}
