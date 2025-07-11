package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper objectMapper; 
    

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("test method findById with ok response")
    public void testFindById_willRetenurOkResponse() throws Exception{
    	
	 	LoginRequest loginRequest = new LoginRequest();
	    loginRequest.setEmail("clarkkent@gmail.com");
	    loginRequest.setPassword("clarkkent");

	    String loginJson = objectMapper.writeValueAsString(loginRequest);
	    
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginJson))
        .andExpect(status().isOk())
        .andReturn();

		String loginResponseBody = loginResult.getResponse().getContentAsString();
		String token = objectMapper.readTree(loginResponseBody).get("token").asText();
	    
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", 2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Session de yoga"));
    }

}