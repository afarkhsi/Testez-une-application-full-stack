package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class UserUnitTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setEmail("John@Wick.com");
        user.setLastName("Wick");
        user.setFirstName("John");
        user.setPassword("Johnwick");
        user.setAdmin(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("John@Wick.com", user.getEmail());
        assertEquals("Wick", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("Johnwick", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime created = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime updated = LocalDateTime.of(2024, 1, 1, 12, 0);

        User user = new User(
                1L,
                "John@Wick.com",
                "Wick",
                "John",
                "Johnwick",
                true,
                created,
                updated
        );

        assertEquals(1L, user.getId());
        assertEquals("John@Wick.com", user.getEmail());
        assertEquals("Wick", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("Johnwick", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(created, user.getCreatedAt());
        assertEquals(updated, user.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        User user = User.builder()
                .id(2L)
                .email("John@Wick.com")
                .lastName("Wick")
                .firstName("John")
                .password("Johnwick")
                .admin(false)
                .createdAt(created)
                .updatedAt(updated)
                .build();

        assertEquals("John@Wick.com", user.getEmail());
        assertEquals("Wick", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("Johnwick", user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals(created, user.getCreatedAt());
        assertEquals(updated, user.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
    	 
    	User user1 = User.builder().id(1L).email("user1@example.com").firstName("John").lastName("Test").password("Johnwick") .build();
    	User user2 = User.builder().id(1L).email("user1@example.com").firstName("John").lastName("Test").password("Johnwick") .build();
    	User user3 = User.builder().id(2L).email("user2@example.com").firstName("John").lastName("Test").password("Johnwick") .build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "not a user");
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        User user = User.builder()
                .id(3L)
                .email("John@test.com")
                .firstName("John")
                .lastName("Test")
                .password("Johnwick") 
                .build();

        String str = user.toString();
        assertTrue(str.contains("John@test.com"));
        assertTrue(str.contains("John"));
        assertTrue(str.contains("Test"));
    }
    
    @Test
    void testRequiredArgsConstructor() {
        // Given
        String email = "JohnWick@gmail.com";
        String lastName = "Wick";
        String firstName = "John";
        String password = "Johnwick";
        boolean admin = true;

        // When
        User user = new User(email, lastName, firstName, password, admin);

        // Then
        assertEquals(email, user.getEmail());
        assertEquals(lastName, user.getLastName());
        assertEquals(firstName, user.getFirstName());
        assertEquals(password, user.getPassword());
        assertTrue(user.isAdmin());

        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }
    
    
    @Test
    void testEqualsWithNullId() {
        User user1 = User.builder().id(null).email("a@b.com").firstName("John").lastName("Test").password("pw").build();
        User user2 = User.builder().id(null).email("a@b.com").firstName("John").lastName("Test").password("pw").build();

        // Deux objets avec id null doivent être égaux (car lombok equals compare id avec Objects.equals())
        assertTrue(user1.equals(user2));
        assertEquals(user1.hashCode(), user2.hashCode());

        User user3 = User.builder().id(1L).email("c@d.com").firstName("Jane").lastName("Doe").password("pw").build();

        assertFalse(user1.equals(user3));
        assertFalse(user3.equals(user1));
    }
    
    @Test
    void testToStringWithNullFields() {
        User user = new User();
        String str = user.toString();
        assertNotNull(str);
        // On peut vérifier que le toString contient les noms des champs même si null
        assertTrue(str.contains("email"));
        assertTrue(str.contains("firstName"));
    }
    
 
}