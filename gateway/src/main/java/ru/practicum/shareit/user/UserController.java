package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос на создание пользователя с данными: {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive Integer userId, @RequestBody @Valid UpdateUserDto updateUserDto) {
        log.info("Получен запрос на обновление пользователя с данными: {}", updateUserDto);
        return userClient.update(userId, updateUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Integer userId) {
        log.info("Получен запрос на получение пользователя по id: {}", userId);
        return userClient.get(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive Integer userId) {
        log.info("Получен запрос на удаление пользователя по id: {}", userId);
        return userClient.delete(userId);
    }

}
