package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Получен запрос на создание пользователя с данными {}", userDto);
        existEmail(userDto.getEmail());
        User user = userRepository.save(userMapper.mapToModel(userDto));
        log.info("Пользователь успешно добавлен, id = {}", user.getId());
        return userMapper.mapToDto(user);
    }

    @Override
    public UserDto update(Integer userId, UpdateUserDto updateUserDto) {
        log.debug("Получен запрос на обновление пользователя {}, с данными {}", userId, updateUserDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().equals(user.getEmail())) {
            existEmail(updateUserDto.getEmail());
        }
        userMapper.mapToModelFromUpdatedUser(updateUserDto, user);
        try {
            userRepository.save(user);
            log.info("Пользователь с id = {} обновлен!", userId);
            return userMapper.mapToDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email уже занят (конфликт при сохранении)");
        }
    }

    @Override
    public UserDto get(Integer userId) {
        log.debug("Получен запрос на получение пользователя по id = {}", userId);
        log.info("Пользователь с id = {}, возвращен", userId);
        return userRepository.findById(userId)
                .map(userMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
    }

    @Override
    public void delete(Integer userId) {
        log.debug("Получен запрос на удаление пользователя по id = {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        userRepository.deleteById(userId);
    }

    private void existEmail(String email) {
        boolean hasEmail = userRepository.existsByEmail(email);
        if (hasEmail) {
            log.error("Пользователь с данным email = {}, уже существует!", email);
            throw new DuplicateEmailException("Пользователь с данным email уже существует");
        }
    }
}

