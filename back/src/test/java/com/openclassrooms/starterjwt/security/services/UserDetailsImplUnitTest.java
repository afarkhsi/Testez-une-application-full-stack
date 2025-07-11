package com.openclassrooms.starterjwt.security.services;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDetailsImplUnitTest {

    @Test
    void testGettersAndBooleans() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("user")
                .firstName("First")
                .lastName("Last")
                .admin(true)
                .password("secret")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("First", user.getFirstName());
        assertEquals("Last", user.getLastName());
        assertTrue(user.getAdmin());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user3 = UserDetailsImpl.builder().id(2L).build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());
    }
}
