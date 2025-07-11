package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Before("")
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("test findById with ok response")
    void testFindById_responseEntityOK() throws Exception{
        String email = "clarkkent@gmail.com";
        String password = "clarkkent";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/teacher/{id}", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("DELAHAYE"))
                .andReturn();
    }

    @Test
    @DisplayName("test findById with a not found response")
    void testFindById_responseEntityNotFound() throws Exception{
        String email = "clarkkent@gmail.com";
        String password = "clarkkent";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);
        this.mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/teacher/{id}", "5")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}