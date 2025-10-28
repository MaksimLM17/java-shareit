package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * REST контроллер для управления пользователями.
 * <p>
 * Предоставляет HTTP endpoints для операций CRUD (Create, Read, Update, Delete)
 * с пользователями системы. Обрабатывает входящие HTTP запросы и делегирует
 * выполнение бизнес-логики сервисному слою.
 * </p>
 *
 * <p><b>Базовый путь:</b> {@code /users}</p>
 *
 * <p><b>Поддерживаемые операции:</b></p>
 * <ul>
 *   <li>Создание нового пользователя</li>
 *   <li>Получение информации о пользователе</li>
 *   <li>Обновление данных пользователя</li>
 *   <li>Удаление пользователя</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see UserService
 * @see UserDto
 * @see UpdateUserDto
 * @since 2025
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    /**
     * Сервис для выполнения бизнес-логики пользователей.
     */
    private final UserService userService;

    /**
     * Создает нового пользователя.
     * <p>
     * Принимает JSON с данными пользователя и сохраняет его в системе.
     * </p>
     *
     * @param userDto DTO объект с данными для создания пользователя
     * @return UserDto созданный пользователь с присвоенным идентификатором
     * @throws ru.practicum.shareit.exception.DuplicateEmailException если пользователь с таким email уже существует
     * @throws org.springframework.dao.DataIntegrityViolationException при конфликте целостности данных
     *
     * @apiNote <b>HTTP запрос:</b> POST /users
     * @apiNote <b>Пример тела запроса:</b>
     * <pre>
     * {
     *   "name": "Иван Иванов",
     *   "email": "ivan@example.com"
     * }
     * </pre>
     *
     * @see UserService#create(UserDto)
     */
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    /**
     * Обновляет данные существующего пользователя.
     * <p>
     * Выполняет частичное обновление данных пользователя. Обновляются только те поля,
     * которые переданы в запросе и не равны null.
     * </p>
     *
     * @param userId идентификатор пользователя для обновления
     * @param updateUserDto DTO объект с данными для обновления
     * @return UserDto обновленный пользователь
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь с указанным id не найден
     * @throws ru.practicum.shareit.exception.DuplicateEmailException если новый email уже занят другим пользователем
     *
     * @apiNote <b>HTTP запрос:</b> PATCH /users/{userId}
     * @apiNote <b>Пример тела запроса:</b>
     * <pre>
     * {
     *   "name": "Новое имя",
     *   "email": "new-email@example.com"
     * }
     * </pre>
     *
     * @see UserService#update(Integer, UpdateUserDto)
     */
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable  Integer userId,
                          @RequestBody UpdateUserDto updateUserDto) {
        return userService.update(userId, updateUserDto);
    }

    /**
     * Возвращает пользователя по идентификатору.
     * <p>
     * Выполняет поиск пользователя в системе и возвращает его данные.
     * </p>
     *
     * @param userId идентификатор запрашиваемого пользователя
     * @return UserDto данные пользователя
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь с указанным id не найден
     *
     * @apiNote <b>HTTP запрос:</b> GET /users/{userId}
     * @apiNote <b>Пример ответа:</b>
     * <pre>
     * {
     *   "id": 1,
     *   "name": "Иван Иванов",
     *   "email": "ivan@example.com"
     * }
     * </pre>
     *
     * @see UserService#get(Integer)
     */
    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Integer userId) {
        return userService.get(userId);
    }

    /**
     * Удаляет пользователя из системы.
     * <p>
     * Удаляет пользователя и все связанные с ним данные в соответствии
     * с настройками каскадного удаления.
     * </p>
     *
     * @param userId идентификатор пользователя для удаления
     * @throws ru.practicum.shareit.exception.NotFoundException если пользователь с указанным id не найден
     *
     * @apiNote <b>HTTP запрос:</b> DELETE /users/{userId}
     * @apiNote <b>HTTP статус ответа:</b> 204 No Content (при успешном удалении)
     *
     * @see UserService#delete(Integer)
     */
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId) {
        userService.delete(userId);
    }
}
