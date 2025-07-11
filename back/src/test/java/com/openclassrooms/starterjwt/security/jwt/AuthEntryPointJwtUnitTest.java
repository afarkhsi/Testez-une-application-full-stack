package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.security.testutils.ServletOutputStreamMock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.MediaType;


public class AuthEntryPointJwtUnitTest {

    private AuthEntryPointJwt authEntryPointJwt;

    @BeforeEach
    public void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
    }

    @Test
    public void testEntryPoint_shouldReturnUnauthorizedJsonResponse() throws IOException, ServletException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(request.getServletPath()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Unauthorized access");

        ServletOutputStreamMock outputStreamMock = new ServletOutputStreamMock();
        when(response.getOutputStream()).thenReturn(outputStreamMock);

        // Act
        authEntryPointJwt.commence(request, response, authException);

        // Assert
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseBody = mapper.readValue(outputStreamMock.getWrittenContent(), Map.class);

        assertEquals(401, responseBody.get("status"));
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Unauthorized access", responseBody.get("message"));
        assertEquals("/api/test", responseBody.get("path"));
    }
}
