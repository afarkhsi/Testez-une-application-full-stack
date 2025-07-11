package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerUnitTest {

    @InjectMocks
    private TeacherController teacherController;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private TeacherService teacherService;

    @BeforeEach
    public void setUp(){
        teacherController = new TeacherController(teacherService, teacherMapper);
    }

 
    @Test
    @DisplayName("test method findAll with ok response")
    void testTeacherList_willReturnOkResponse(){
        List<Teacher> teacherList = new ArrayList<>();
        List<TeacherDto> teacherDtoList = new ArrayList<>();

        when(teacherService.findAll()).thenReturn(teacherList);
        when(teacherMapper.toDto(teacherList)).thenReturn(teacherDtoList);

        ResponseEntity<?> findAll = teacherController.findAll();
        ResponseEntity<?> responseEntityOK = ResponseEntity.ok().body(teacherDtoList);

        assertEquals(findAll,responseEntityOK);
        verify(teacherMapper, times(1)).toDto(teacherList);
    }
}