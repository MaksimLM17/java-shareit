package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public User mapToModel(UserDto userDto);

    public UserDto mapToDto(User user);

    default User mapToModelFromUpdatedUser(UpdateUserDto userDto, User user) {
        if (userDto == null) {
            return user;
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}
