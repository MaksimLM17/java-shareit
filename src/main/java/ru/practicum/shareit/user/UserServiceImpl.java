package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Получен запрос на создание пользователя с данными {}", userDto);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Пользователь с данным email = {}, уже существует", userDto.getEmail());
            throw new DuplicateEmailException("Пользователь с данным email уже существует");
        }
        User user = userRepository.create(UserMapper.mapToModel(userDto));
        log.info("Пользователь успешно добавлен, id = {}", user.getId());
        return UserMapper.mapToDto(user);
    }

    @Override
    public UserDto update(Integer userId, UpdateUserDto updateUserDto) {
        log.debug("Получен запрос на обновление пользователя {}, с данными {}", userId, updateUserDto);
        if (!userRepository.checkUser(userId)) {
            log.error("Пользователь с id {} не найден! При запросе на обновление пользователя", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        if (updateUserDto.getEmail() != null) {
            if (userRepository.existsByEmail(updateUserDto.getEmail())) {
                log.error("Пользователь с данным email = {}, уже существует!", updateUserDto.getEmail());
                throw new DuplicateEmailException("Пользователь с данным email уже существует");
            }
        }
        User user = userRepository.update(userId, updateUserDto);
        log.info("Пользователь с id = {} обновлен!", userId);
        return UserMapper.mapToDto(user);
    }

    @Override
    public UserDto get(Integer userId) {
        log.debug("Получен запрос на получение пользователя по id = {}", userId);
        if (!userRepository.checkUser(userId)) {
            log.error("Пользователь с id {} не найден! При запросе на получение пользователя", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        log.info("Пользователь с id = {}, возвращен", userId);
        return UserMapper.mapToDto(userRepository.get(userId));
    }

    @Override
    public void delete(Integer userId) {
        log.debug("Получен запрос на удаление пользователя по id = {}", userId);
        if (!userRepository.checkUser(userId)) {
            log.error("Пользователь с id {} не найден! При запросе на удаление пользователя", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        userRepository.delete(userId);
    }
}
