package ru.practicum.shareit.user;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1, "Алексей", "alex434@example.com");
    private final UpdateUserDto updateUserDto = new UpdateUserDto();
    private final User user = new User();

    @Test
    void create_shouldSaveNewUser() {
        when(userMapper.mapToModel(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void create_shouldThrowExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.create(userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateUserData() {
        updateUserDto.setName("Updated Name");
        updateUserDto.setEmail("updated@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto result = userService.update(1, updateUserDto);

        assertNotNull(result);
        verify(userMapper).mapToModelFromUpdatedUser(updateUserDto, user);
        verify(userRepository).save(user);
    }

    @Test
    void update_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1, updateUserDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowExceptionWhenEmailExists() {
        updateUserDto.setEmail("existing@example.com");
        user.setEmail("old@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.update(1, updateUserDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldHandleDataIntegrityViolation() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("Duplicate email"));

        assertThrows(DuplicateEmailException.class, () -> userService.update(1, updateUserDto));
    }

    @Test
    void get_shouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        UserDto result = userService.get(1);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    void get_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.get(1));
    }

    @Test
    void delete_shouldDeleteUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.delete(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void delete_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1));
        verify(userRepository, never()).deleteById(any());
    }
}
