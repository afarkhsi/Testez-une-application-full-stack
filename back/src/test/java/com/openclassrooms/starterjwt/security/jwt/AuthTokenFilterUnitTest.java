package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthTokenFilterUnitTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @Tag("AuthTokenFilter_doFilterInternal")
    @DisplayName("Authentication test with Valid Token")
    public void testDoFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("validToken")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("validToken")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null); // Or mock authorities if needed

        authTokenFilter.doFilterInternal(request, response, filterChain);

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Tag("AuthTokenFilter_doFilterInternal")
    @DisplayName("Authentication Test with Invalid Token")
    public void testDoFilterInternal_WithInvalidToken_ShouldNotAuthenticate() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken("invalidToken")).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @Tag("AuthTokenFilter_parseJwt")
    @DisplayName("Parsing with valid token header test")
    public void testParseJwt_WithValidHeader_ShouldReturnToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer someValidToken");

        String token = authTokenFilterTestProxy().parseJwt(request);
        assertEquals("someValidToken", token);
    }

    @Test
    @Tag("AuthTokenFilter_parseJwt")
    @DisplayName("Parsing with null token header test")
    public void testParseJwt_WithNoAuthorizationHeader_ShouldReturnNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token = authTokenFilterTestProxy().parseJwt(request);
        assertNull(token);
    }

    // Trick to access protected method parseJwt for testing
    private AuthTokenFilter authTokenFilterTestProxy() {
        return new AuthTokenFilter() {
            public String parseJwt(MockHttpServletRequest request) {
                return super.parseJwt(request);
            }
        };
    }
}
