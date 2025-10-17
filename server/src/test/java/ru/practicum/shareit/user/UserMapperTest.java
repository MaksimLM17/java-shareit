package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void mapToModel_shouldMapUserDtoToUser() {
        UserDto userDto = new UserDto(1, "Артем", "artem@example.com");

        User user = userMapper.mapToModel(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void mapToDto_shouldMapUserToUserDto() {
        User user = new User(1, "Артем", "artem@example.com");

        UserDto userDto = userMapper.mapToDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void mapToModelFromUpdatedUser_shouldUpdateNameWhenNotNull() {
        User existingUser = new User(1, "Old Name", "old@example.com");
        UpdateUserDto updateDto = new UpdateUserDto("New Name", null);

        User updatedUser = userMapper.mapToModelFromUpdatedUser(updateDto, existingUser);

        assertEquals("New Name", updatedUser.getName());
        assertEquals("old@example.com", updatedUser.getEmail());
    }

    @Test
    void mapToModelFromUpdatedUser_shouldUpdateEmailWhenNotNull() {
        User existingUser = new User(1, "Old Name", "old@example.com");
        UpdateUserDto updateDto = new UpdateUserDto(null, "new@example.com");

        User updatedUser = userMapper.mapToModelFromUpdatedUser(updateDto, existingUser);

        assertEquals("Old Name", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
    }

    @Test
    void mapToModelFromUpdatedUser_shouldUpdateBothFieldsWhenNotNull() {
        User existingUser = new User(1, "Old Name", "old@example.com");
        UpdateUserDto updateDto = new UpdateUserDto("New Name", "new@example.com");

        User updatedUser = userMapper.mapToModelFromUpdatedUser(updateDto, existingUser);

        assertEquals("New Name", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
    }
}
