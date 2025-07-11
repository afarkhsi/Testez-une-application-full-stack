package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeacherUnitTest {

    @Test
    void testTeacherEntity(){
        Teacher teacher = new Teacher();
        teacher.equals(new Teacher());
        teacher.hashCode();
        teacher.toString();
        assertNotNull(teacher.toString());
    }

    @Test
    void testTeacherEntityBuilder(){
        Teacher teacher = new Teacher();
        teacher.equals(Teacher.builder().build());
        assertNotNull(teacher.toString());
    }
    
    @Test
    void testEqualsAndHashCodeAndToString() {
        Teacher teacher1 = Teacher.builder().id(1L).build();
        Teacher teacher2 = Teacher.builder().id(1L).build();
        Teacher teacher3 = Teacher.builder().id(2L).build();
        Teacher teacherNullId1 = Teacher.builder().build();
        Teacher teacherNullId2 = Teacher.builder().build();

        // equals: same instance
        assertTrue(teacher1.equals(teacher1));

        // equals: equal ids
        assertTrue(teacher1.equals(teacher2));
        assertEquals(teacher1.hashCode(), teacher2.hashCode());

        // equals: different ids
        assertFalse(teacher1.equals(teacher3));

        // equals: one id null, other not null
        assertFalse(teacher1.equals(teacherNullId1));
        assertFalse(teacherNullId1.equals(teacher1));

        // equals: both ids null (should be equal according to lombok equals if no id?)
        // Actually Lombok compares fields by Objects.equals(), so null == null true
        assertTrue(teacherNullId1.equals(teacherNullId2));
        assertEquals(teacherNullId1.hashCode(), teacherNullId2.hashCode());

        // equals: compared with null or different class
        assertFalse(teacher1.equals(null));
        assertFalse(teacher1.equals("some string"));

        // toString not null
        assertNotNull(teacher1.toString());
    }
}