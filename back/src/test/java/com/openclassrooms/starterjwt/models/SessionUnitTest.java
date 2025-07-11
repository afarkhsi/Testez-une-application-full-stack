package com.openclassrooms.starterjwt.models;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SessionUnitTest {

    @Test
    void testSessionEntity(){
        Session session = new Session();
        session.equals(new Session());
        session.hashCode();
        session.toString();
        assertNotNull(session.toString());
    }

    @Test
    void testSessionEntityBuilder() {
        // Given
        String name = "Session de Test";
        Date date = new Date();
        String description = "Description";
        Teacher teacher = new Teacher();
        List<User> users = Arrays.asList(new User(), new User());

        Session session = Session.builder()
                .name(name)
                .date(date)
                .description(description)
                .teacher(teacher)
                .users(users)
                .build();

        assertEquals(name, session.getName());
        assertEquals(date, session.getDate());
        assertEquals(description, session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());

        assertNull(session.getId());
        assertNull(session.getCreatedAt());
        assertNull(session.getUpdatedAt());
    }
    
    @Test
    void testNoArgsConstructorAndSetters() {
        Session session = new Session();
        LocalDateTime now = LocalDateTime.now();
        Date date = new Date();

        session.setId(1L);
        session.setName("Test Session");
        session.setDate(date);
        session.setDescription("Description");
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        Teacher teacher = new Teacher();
        session.setTeacher(teacher);

        List<User> users = Arrays.asList(new User(), new User());
        session.setUsers(users);

        assertEquals(1L, session.getId());
        assertEquals("Test Session", session.getName());
        assertEquals(date, session.getDate());
        assertEquals("Description", session.getDescription());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());
    }
    
    @Test
    void testAllArgsConstructor() {
        LocalDateTime created = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 1, 10, 0);
        Date date = new Date();

        Teacher teacher = new Teacher();
        List<User> users = Arrays.asList(new User());

        Session session = new Session(
                1L,
                "Arguments Session",
                date,
                "Description complète",
                teacher,
                users,
                created,
                updated
        );

        assertEquals("Arguments Session", session.getName());
        assertEquals(date, session.getDate());
        assertEquals("Description complète", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());
        assertEquals(created, session.getCreatedAt());
        assertEquals(updated, session.getUpdatedAt());
    }
    
    @Test
    void testSessionEqualsHashcode() {
        Session s1 = new Session();
        Session s2 = new Session();

        s1.setId(1L);
        s2.setId(1L);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
    
    @Test
    void testToString() {
        Session session = Session.builder()
                .id(100L)
                .name("Test string Session")
                .description("Test string")
                .build();

        String str = session.toString();
        assertTrue(str.contains("Test string Session"));
        assertTrue(str.contains("Test string"));
    }
}