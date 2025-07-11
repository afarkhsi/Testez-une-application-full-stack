package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @BeforeEach
    public void setUp(){
        userController = new UserController(userService,userMapper);
    }

    @Test
    @DisplayName("test method findById with ok response")
    void testFindUser_willReturnOkResponseEntity(){
        String id = "4";
        User user = new User();
        UserDto userDto = new UserDto();

        when(userService.findById(Long.valueOf(id))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> findById = userController.findById(id);
        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().body(userDto);

        assertEquals(findById, responseEntityOK);
        verify(userService, times(1)).findById(Long.parseLong(id));
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("test method findById with notfound response")
    void testUserNull_willReturnNotFoundResponse(){
        String id = "4";
        User user = null;

        when(userService.findById(Long.parseLong(id))).thenReturn(user);

        ResponseEntity<?> findById = userController.findById(id);
        ResponseEntity<?> responseEntityNotFound = ResponseEntity.notFound().build();

        assertEquals(findById,responseEntityNotFound);
        verify(userService, times(1)).findById(Long.parseLong(id));
    }

    @Test
    @DisplayName("test method findById with bad request response")
    void testIdInvalidFormat_willReturnBadRequestResponse(){
        String id = "incorrect_id";
        assertThrows(NumberFormatException.class, () -> {Long.valueOf(id);});
        ResponseEntity<?> findById = userController.findById(id);
        ResponseEntity<?> badRequestResponse = ResponseEntity.badRequest().build();
        assertEquals(findById,badRequestResponse);
    }

    @Test
    @DisplayName("test save method with ok response")
    void testDeleteUser_willReturnOkResponse(){
        String id = "5";
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setEmail("clarkkent@gmail.Com");

        when(userService.findById(Long.valueOf(id))).thenReturn(user);

        UserDetails userDetails = UserDetailsImpl.builder().username(user.getEmail()).build();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        userService.delete(Long.parseLong(id));
        ResponseEntity<?> save = userController.save(id);
        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().build();
        assertEquals(save,responseEntityOK);
        verify(userService, times(1)).findById(Long.valueOf(id));
        verify(userService, times(2)).delete(Long.valueOf(id));
    }

    @Test
    @DisplayName("test save method with not found response")
    void testUserNull_willReturnNotFoundResponseEntityNot(){
        String id = "87";
        User user = null;

        when(userService.findById(Long.valueOf(id))).thenReturn(user);

        ResponseEntity<?> save = userController.save(id);
        ResponseEntity<?> responseEntityNotFound = ResponseEntity.notFound().build();

        assertEquals(save, responseEntityNotFound);
        verify(userService, times(1)).findById(Long.valueOf(id));
    }

    @Test
    @DisplayName("test save method with unauthorized response")
    void testIncorrectUserInformations_willReturnUnauthorizedResponse(){
        String id = "5";
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setEmail("clarkkent@gmail.com");
        user.setPassword("clark");
        user.setFirstName("Kent");
        user.setLastName("Clark");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.parse("2025-07-11T21:39:32"));
        user.setUpdatedAt(LocalDateTime.parse("2025-07-11T23:39:33"));

        when(userService.findById(Long.valueOf(id))).thenReturn(user);

        // Different email to cause unauthorized response
        UserDetails userDetails = UserDetailsImpl.builder().username("batman@gmail.com").build();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        ResponseEntity<?> save = userController.save(id);
        assertEquals(save.getStatusCode(),HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("test save method with bad request response")
    void testIdInvalidFormat_willReturnBadRequestResponsee(){
        String id = "incorrect_id";
        assertThrows(NumberFormatException.class, () -> { Long.valueOf(id); });
        ResponseEntity<?> save = userController.save(id);
        ResponseEntity<?> badRequestResponse = ResponseEntity.badRequest().build();
        assertEquals(save,badRequestResponse);
    }
}