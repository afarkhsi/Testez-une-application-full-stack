package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TeacherMapperUnitTest {

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    void teacherDtoToTeacherEntity(){
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(3L);
        teacherDto.setFirstName("firstname");
        teacherDto.setLastName("lastname");

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertNotNull(teacher);
        assertEquals(teacherDto.getId(),teacher.getId());
    }

    @Test
    void teacherEntityToTeacherDto(){
        Teacher teacher = new Teacher();
        teacher.setId(3L);
        teacher.setFirstName("firstname");
        teacher.setLastName("lastname");

        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        assertNotNull(teacherDto);
        assertEquals(teacherDto.getId(),teacher.getId());
    }

    @Test
    void teacherDtoListToTeacherEntityList(){
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(3L);
        teacherDto.setFirstName("firstname");
        teacherDto.setLastName("lastname");

        List<TeacherDto> teacherDtoList = new ArrayList<>();
        teacherDtoList.add(teacherDto);

        List<Teacher> teacherList = teacherMapper.toEntity(teacherDtoList);
        assertNotNull(teacherList);
        assertEquals(teacherDtoList.get(0).getId(), teacherList.get(0).getId());
    }

    @Test
    void teacherEntityListToTeacherDtoList(){
        Teacher teacher = new Teacher();
        teacher.setId(3L);
        teacher.setFirstName("firstname");
        teacher.setLastName("lastname");

        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(teacher);

        List<TeacherDto> teacherDtoList = teacherMapper.toDto(teacherList);
        assertNotNull(teacherDtoList);
        assertEquals(teacherList.get(0).getId(), teacherDtoList.get(0).getId());
    }
}