package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionControllerUnitTest {

    @InjectMocks
    private SessionController sessionController;

    @Mock
    private SessionMapper sessionMapper;

    @Mock
    private SessionService sessionService;

    @BeforeEach
    public void setUp(){
        sessionController = new SessionController(sessionService,sessionMapper);
    }

    @Test
    @DisplayName("test method findById with ok response")
    void testUserId_willReturnOkResponse(){
        String id = "5";
        Session session = new Session();
        SessionDto sessionDto = new SessionDto();

        when(sessionService.getById(Long.valueOf(id))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().body(sessionDto);
        ResponseEntity<?> findById = sessionController.findById(id);

        assertEquals(findById.getStatusCode(), HttpStatus.OK);
        assertEquals(findById, responseEntityOK);
    }

    @Test
    @DisplayName("test method findById with notfound response")
    void testNullSession_willReturnNotFoundResponse(){
        String id = "99";
        Session session = null;

        when(sessionService.getById(Long.valueOf(id))).thenReturn(session);
        ResponseEntity<?> findById = sessionController.findById(id);
        assertEquals(findById.getStatusCode(),  HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("test method findById with bad request response")
    void testIncorredId_willReturnBadRequestResponse(){
        String id = "incorrect_id";

        assertThrows(NumberFormatException.class, () -> { Long.valueOf(id); });
        ResponseEntity<?> findById = sessionController.findById(id);
        ResponseEntity<?> badRequestResponse = ResponseEntity.badRequest().build();
        assertEquals(findById, badRequestResponse);
    }

    @Test
    @DisplayName("test method findAll with ok response")
    void testSessionList_willReturnOkResponse(){
        List<Session> sessions = new ArrayList<>();
        when(sessionService.findAll()).thenReturn(sessions);
        List<SessionDto> sessionDto = sessionMapper.toDto(sessions);

        ResponseEntity<?> responseEntityOK = ResponseEntity.ok(sessionDto);
        ResponseEntity<?> findAll = sessionController.findAll();

        assertEquals(findAll, responseEntityOK);
    }

    @Test
    @DisplayName("test method create with ok response")
    void testSessionCreate_willReturnOkResponse(){
        Session session = new Session();
        SessionDto sessionDto = new SessionDto();

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);

        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().body(sessionMapper.toDto(session));
        ResponseEntity<?> create = sessionController.create(sessionDto);

        assertEquals(create, responseEntityOK);
    }

    @Test
    @DisplayName("test method update with ok response")
    void testSessionUpdate_willReturnOkResponse(){
        String id = "1";
        Session session = new Session();
        SessionDto sessionDto = new SessionDto();

        when(sessionService.update(Long.parseLong(id), sessionMapper.toEntity(sessionDto))).thenReturn(session);

        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().body(sessionMapper.toDto(session));
        ResponseEntity<?> update = sessionController.update(id,sessionDto);

        assertEquals(update, responseEntityOK);
    }

    @Test
    @DisplayName("test update method with bad request response ")
    void testInvalidUpdate_willReturnBadRequestResponse(){
        String id = "incorrect_id";
        SessionDto sessionDto = new SessionDto();

        assertThrows(NumberFormatException.class, () -> { Long.valueOf(id);});
        ResponseEntity<?> update = sessionController.update(id, sessionDto);
        ResponseEntity<?> responseEntityBadRequest = ResponseEntity.badRequest().build();
        assertEquals(update, responseEntityBadRequest);
    }

    @Test
    @DisplayName("test save method with bad request response")
    void testSessionSave_willReturnOkResponse(){
        String id = "1";
        Session session = new Session();
        when(sessionService.getById(Long.valueOf(id))).thenReturn(session);
        sessionService.delete(Long.valueOf(id));
        ResponseEntity<?> save = sessionController.save(id);
        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().build();
        assertEquals(save, responseEntityOK);
        verify(sessionService, times(2)).delete(Long.valueOf(id));
    }

    @Test
    @DisplayName("test save method with not found response")
    void testSessionDelete_willReturnNotFoundResponse(){
        String id = "0";
        Session session = null;

        when(sessionService.getById(Long.valueOf(id))).thenReturn(session);

        ResponseEntity<?> responseEntityNotFound = ResponseEntity.notFound().build();
        ResponseEntity<?> save = sessionController.save(id);

        assertEquals(save, responseEntityNotFound);
    }

    @Test
    @DisplayName("test save method with bad request response")
    void testSessionDeleteWithIncorrectID_willReturnBadRequestResponse(){
        String id = "incorrect_id";
        assertThrows(NumberFormatException.class, () -> {Long.valueOf(id);});
        ResponseEntity<?> save = sessionController.save(id);
        ResponseEntity<?> responseEntityBadRequest = ResponseEntity.badRequest().build();
        assertEquals(save,responseEntityBadRequest);
    }

    @Test
    @DisplayName("test participate method with ok response")
    void testParticipate_willReturnOkResponse(){
        String id = "1";
        String userId = "1";

        sessionService.participate(Long.parseLong(id), Long.parseLong(userId));
        assertEquals(sessionController.participate(id,userId), ResponseEntity.ok().build());
        verify(sessionService, times(2)).participate(Long.parseLong(id), Long.parseLong(userId));
    }

    @Test
    @DisplayName(" test participate method with bad request response")
    void testParticipateWithIncorrectIdValues_willReturnBadRequestResponse(){
        String id = "incorrect_id";
        String userId = "incorrect_userId";

        assertThrows(NumberFormatException.class, () -> {Long.valueOf(id);});

        ResponseEntity<?> participate = sessionController.participate(id, userId);
        ResponseEntity<?> responseEntityBadRequest = ResponseEntity.badRequest().build();
        assertEquals(participate,responseEntityBadRequest);
    }

    @Test
    @DisplayName("test noLongerParticipate method with ok response")
    void testnoLongerParticipate_willReturnOkResponse(){
        String id = "1";
        String userId = "1";

        sessionService.noLongerParticipate(Long.parseLong(id), Long.parseLong(userId));
        assertEquals(sessionController.noLongerParticipate(id,userId), ResponseEntity.ok().build());
        verify(sessionService, times(2)).noLongerParticipate(Long.parseLong(id), Long.parseLong(userId));
    }

    @Test
    @DisplayName("test noLongerParticipate method with bad request response")
    void whenIdAndUserIdInvalid_thenReturnResponseEntityBadRequestNoParticipate(){
        String id = "incorrect_id";
        String userId = "incorrect_userId";

        assertThrows(NumberFormatException.class, () -> {Long.valueOf(id);});

        ResponseEntity<?> noLongerParticipate = sessionController.noLongerParticipate(id, userId);
        ResponseEntity<?> responseEntityBadRequest = ResponseEntity.badRequest().build();
        assertEquals(noLongerParticipate,responseEntityBadRequest);
    }
}