package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * DTO (Data Transfer Object) класс для передачи данных о пользователе.
 * <p>
 * Используется для обмена данными между клиентом и сервером через REST API.
 * Содержит только необходимые поля для отображения и создания пользователей.
 * Не содержит бизнес-логики и аннотаций JPA.
 * </p>
 * @author MaksimLM17
 * @version 1.0
 * @see ru.practicum.shareit.user.UserController
 * @see ru.practicum.shareit.user.UserService
 * @since 2025
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String name;
    @Email(message = "Некорректное значение email!")
    private String email;
}
