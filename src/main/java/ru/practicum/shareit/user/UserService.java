package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Integer userId, UpdateUserDto updateUserDto);

    UserDto get(Integer userId);

    void delete(Integer userId);
}
