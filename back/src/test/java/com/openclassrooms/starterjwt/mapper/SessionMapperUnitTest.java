package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SessionMapperUnitTest {

    @Autowired
    private SessionMapper sessionMapper;

    @Test
    void sessionDtoToSession() {
        Teacher teacher = new Teacher();
        teacher.setId(3L);

        User user = new User();
        user.setId(1L);

        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setDescription("description");
        sessionDto.setName("Yoga Flow");
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setUsers(Arrays.asList(user.getId()));
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setUpdatedAt(LocalDateTime.now());

        Session session = sessionMapper.toEntity(sessionDto);

        assertNotNull(session);
        assertEquals(sessionDto.getId(), session.getId());
        assertEquals(sessionDto.getDescription(), session.getDescription());
        assertEquals(sessionDto.getName(), session.getName());
        assertEquals(sessionDto.getDate(), session.getDate());
        assertEquals(sessionDto.getCreatedAt(), session.getCreatedAt());
        assertEquals(sessionDto.getUpdatedAt(), session.getUpdatedAt());
    }

    @Test
    void sessionToSessionDto() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        User user = new User();
        user.setId(1L);

        Session session = new Session();
        session.setId(1L);
        session.setDescription("description");
        session.setName("Yoga Flow");
        session.setTeacher(teacher);
        session.setUsers(Collections.singletonList(user));
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());

        SessionDto sessionDto = sessionMapper.toDto(session);

        assertNotNull(sessionDto);
        assertEquals(session.getId(), sessionDto.getId());
        assertEquals(session.getDescription(), sessionDto.getDescription());
        assertEquals(session.getName(), sessionDto.getName());
        assertEquals(session.getTeacher().getId(), sessionDto.getTeacher_id());
        assertEquals(session.getUsers().get(0).getId(), sessionDto.getUsers().get(0));
    }


    @Test
    void toDto_nullList_returnsNull() {
        List<SessionDto> result = sessionMapper.toDto((List<Session>) null);
        assertNull(result);
    }

    @Test
    void toEntity_listMapping() {
        SessionDto dto = new SessionDto();
        dto.setId(2L);
        dto.setDescription("desc");
        dto.setUsers(Collections.emptyList());

        List<Session> result = sessionMapper.toEntity(Collections.singletonList(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto.getId(), result.get(0).getId());
    }

    @Test
    void toDto_listMapping() {
        Session entity = new Session();
        entity.setId(3L);
        entity.setDescription("mapped");

        List<SessionDto> result = sessionMapper.toDto(Collections.singletonList(entity));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        Session result = sessionMapper.toEntity((SessionDto) null);
        assertNull(result);
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        SessionDto result = sessionMapper.toDto((Session) null);
        assertNull(result);
    }
}