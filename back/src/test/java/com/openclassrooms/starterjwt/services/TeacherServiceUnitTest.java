package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


public class TeacherServiceUnitTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Tag("TeacherService_findAll")
    @DisplayName("Find all teachers")
    public void testFindAllTeachers() {
    	LocalDateTime localDateTime = LocalDateTime.now();
    	
    	List<Teacher> teachers = new ArrayList<>();
    	
	    Teacher teacher = new Teacher();

	    teacher
	        .setId(1L)
	        .setLastName("Kent")
	        .setFirstName("Clark")
	        .setCreatedAt(localDateTime)
	        .setUpdatedAt(localDateTime);
	    
	    Teacher teacher2 = new Teacher();
        teacher2
                .setId(2L)
                .setLastName("Wayne")
                .setFirstName("Bruce")
                .setCreatedAt(localDateTime)
                .setUpdatedAt(localDateTime);
        
        teachers.add(teacher);
        teachers.add(teacher2);

        when(teacherRepository.findAll()).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Clark");
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @Tag("TeacherService_findById")
    @DisplayName("Teacher found by ID")
    public void testFindByIdTeacher() {
    	
		LocalDateTime localDateTime = LocalDateTime.now();
	
	    Long teacherId = 2L;
	    Teacher teacher = new Teacher();
        // Arrange
	    teacher
	        .setId(2L)
	        .setLastName("DELAHAYE")
	        .setFirstName("Margot")
	        .setCreatedAt(localDateTime)
	        .setUpdatedAt(localDateTime);

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.findById(teacherId);

        verify(teacherRepository).findById(teacherId);
        
        // Assert
        assertEquals(teacher, result);
    }
    
    @Test
    @Tag("TeacherService_findById")
    @DisplayName("Teacher not found by ID should return null")
    public void testFindByIdTeacherNotFound() {
        Long teacherId = 99L;

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(teacherId);

        assertNull(result);

        verify(teacherRepository).findById(teacherId);
    }
}
