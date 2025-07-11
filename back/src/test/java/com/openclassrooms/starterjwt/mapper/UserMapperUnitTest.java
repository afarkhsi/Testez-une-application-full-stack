package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperUnitTest {

    @Autowired
    private UserMapper userMapper;

    private User userEntity;

    @BeforeEach
    void setUp(){
        userEntity = new User();
        userEntity.setId(6L);
        userEntity.setEmail("email@test.com");
        userEntity.setPassword("password");
        userEntity.setFirstName("firstname");
        userEntity.setLastName("lastname");
        userEntity.setAdmin(false);
    }

    @Test
    void userDtoToEntity(){
        UserDto userDto = new UserDto();
        userDto.setEmail("email@email.com");
        userDto.setId(7L);
        userDto.setAdmin(false);
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setPassword("password");

        User user = userMapper.toEntity(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(),user.getId());
    }

    @Test
    void userEntityToDTO(){
        UserDto userDto = userMapper.toDto(userEntity);
        assertNotNull(userDto);
        assertEquals(userEntity.getId(),userDto.getId());
    }

    @Test
    void userDtoListToUserEntityList(){
        UserDto userDto = new UserDto();
        userDto.setEmail("email@email.com");
        userDto.setId(7L);
        userDto.setAdmin(false);
        userDto.setFirstName("firstname");
        userDto.setLastName("lastname");
        userDto.setPassword("password");
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);

        List<User> userList = userMapper.toEntity(userDtoList);
        assertNotNull(userList);
        assertEquals(userDtoList.get(0).getId(),userList.get(0).getId());
    }

    @Test
    void userEntityListToUserDtoList(){
        List<User> userList = new ArrayList<>();
        userList.add(userEntity);
        List<UserDto> userDtoList = userMapper.toDto(userList);
        assertNotNull(userDtoList);
        assertEquals(userList.get(0).getId(),userDtoList.get(0).getId());
    }
}