package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO (Data Transfer Object) класс для передачи данных о пользователе.
 * <p>
 * Используется для обмена данными между клиентом и сервером через REST API.
 * Содержит только необходимые поля для обновления пользователей.
 * Обновляются только не null поля.
 * Используется для PATCH запросов.
 * Идентификатор передается в URL пути.
 * </p>
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.user.UserController
 * @see ru.practicum.shareit.user.UserService
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private String name;
    private String email;
}
