package com.openclassrooms.starterjwt.services;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

public class SessionServiceUnitTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Tag("SessionService_findAll")
    @DisplayName("Find all sessions")
    public void testFindAllSessions() {
        LocalDateTime now = LocalDateTime.now();

        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Yoga Basics");
        session1.setCreatedAt(now);
        session1.setUpdatedAt(now);

        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Advanced Yoga");
        session2.setCreatedAt(now);
        session2.setUpdatedAt(now);

        List<Session> sessions = Arrays.asList(session1, session2);

        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Yoga Basics");
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @Tag("SessionService_getById")
    @DisplayName("Session found by ID")
    public void testGetByIdFound() {
        LocalDateTime now = LocalDateTime.now();
        Long id = 1L;

        Session session = new Session();
        session.setId(id);
        session.setName("Yoga Basics");
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        when(sessionRepository.findById(id)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(id);

        verify(sessionRepository).findById(id);
        assertEquals(session, result);
    }

    @Test
    @Tag("SessionService_getById")
    @DisplayName("Session not found by ID returns null")
    public void testGetByIdNotFound() {
        Long id = 99L;

        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        Session result = sessionService.getById(id);

        assertNull(result);
        verify(sessionRepository).findById(id);
    }

    @Test
    @Tag("SessionService_create")
    @DisplayName("Create session")
    public void testCreateSession() {
        Session session = new Session();
        session.setName("New Session");

        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertEquals(session, result);
        verify(sessionRepository).save(session);
    }
    
    @Test
    @Tag("SessionService_update")
    @DisplayName("Update session")
    public void testUpdateSession() {
        Long sessionId = 1L;
        Session sessionToUpdate = new Session();
        sessionToUpdate.setName("Updated Title");

        Session savedSession = new Session();
        savedSession.setId(sessionId);
        savedSession.setName("Updated Title");

        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

        Session result = sessionService.update(sessionId, sessionToUpdate);

        verify(sessionRepository).save(sessionToUpdate);

        assertEquals(sessionId, result.getId());
        assertEquals("Updated Title", result.getName());
    }

    @Test
    @Tag("SessionService_delete")
    @DisplayName("Delete session")
    public void testDeleteSession() {
        Long id = 1L;

        doNothing().when(sessionRepository).deleteById(id);

        sessionService.delete(id);

        verify(sessionRepository).deleteById(id);
    }

    @Test
    @Tag("SessionService_participate")
    @DisplayName("User participates in session successfully")
    public void testParticipateSuccess() {
        Long sessionId = 1L;
        Long userId = 2L;

        Session session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(userId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(sessionId, userId);

        verify(sessionRepository).findById(sessionId);
        verify(userRepository).findById(userId);
        verify(sessionRepository).save(session);

        assertTrue(session.getUsers().contains(user));
    }
    
    @Test
    @DisplayName("participate throws NotFoundException when session is null")
    public void testParticipateThrowsNotFoundExceptionWhenSessionNull() {
        Long sessionId = 1L;
        Long userId = 2L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty()); // session null
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(sessionId, userId);
        });

        verify(sessionRepository).findById(sessionId);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("participate throws NotFoundException when user is null")
    public void testParticipateThrowsNotFoundExceptionWhenUserNull() {
        Long sessionId = 1L;
        Long userId = 2L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(new Session()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty()); // user null

        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(sessionId, userId);
        });

        verify(sessionRepository).findById(sessionId);
        verify(userRepository).findById(userId);
    }
    
    @Test
    @DisplayName("participate throws BadRequestException when user already participates")
    public void testParticipateThrowsBadRequestExceptionWhenUserAlreadyParticipates() {
        Long sessionId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setUsers(new ArrayList<>(Arrays.asList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            sessionService.participate(sessionId, userId);
        });

        verify(sessionRepository).findById(sessionId);
        verify(userRepository).findById(userId);
    }
    
    @Test
    @Tag("SessionService_noLongerParticipate")
    @DisplayName("User no longer participates successfully")
    public void testNoLongerParticipateSuccess() {
        Long sessionId = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);

        Session session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>(Arrays.asList(user)));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(sessionId, userId);

        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(session);

        assertFalse(session.getUsers().contains(user));
    }
    
    @Test
    @DisplayName("noLongerParticipate throws NotFoundException when session is null")
    public void testNoLongerParticipateThrowsNotFoundExceptionWhenSessionNull() {
        Long sessionId = 1L;
        Long userId = 2L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty()); // session null

        assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(sessionId, userId);
        });

        verify(sessionRepository).findById(sessionId);
    }
    
    @Test
    @DisplayName("noLongerParticipate throws BadRequestException when user does not participate")
    public void testNoLongerParticipateThrowsBadRequestExceptionWhenUserNotParticipating() {
        Long sessionId = 1L;
        Long userId = 2L;

        Session session = new Session();
        session.setUsers(new ArrayList<>()); // pas d'utilisateur

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(sessionId, userId);
        });

        verify(sessionRepository).findById(sessionId);
    }
    
}
