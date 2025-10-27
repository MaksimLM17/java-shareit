package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
/**
 * Сервис для управления пользователями.
 * <p>
 * Предоставляет бизнес-логику для операций CRUD (Create, Read, Update, Delete)
 * с пользователями системы.
 * </p>
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *   <li>Создание новых пользователей</li>
 *   <li>Обновление данных существующих пользователей</li>
 *   <li>Получение информации о конкретном пользователе</li>
 *   <li>Удаление пользователей из системы</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see UserDto
 * @see UpdateUserDto
 * @see ru.practicum.shareit.user.UserController
 * @since 2025
 */
public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Integer userId, UpdateUserDto updateUserDto);

    UserDto get(Integer userId);

    void delete(Integer userId);
}
