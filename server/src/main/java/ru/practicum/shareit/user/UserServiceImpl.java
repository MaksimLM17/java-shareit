package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Реализация сервиса для управления пользователями.
 * <p>
 * Предоставляет бизнес-логику для операций с пользователями, включая валидацию,
 * проверку уникальности email и обработку исключительных ситуаций.
 * </p>
 *
 * <p><b>Особенности реализации:</b></p>
 * <ul>
 *   <li>Использует Spring Data JPA репозиторий для работы с базой данных</li>
 *   <li>Применяет маппинг между DTO и entity с помощью UserMapper</li>
 *   <li>Обеспечивает подробное логирование всех операций</li>
 *   <li>Обрабатывает бизнес-исключения (NotFoundException, DuplicateEmailException)</li>
 *   <li>Использует транзакции для гарантии атомарности операций</li>
 * </ul>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see UserService
 * @see UserRepository
 * @see UserMapper
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * Репозиторий для работы с данными пользователей в базе данных.
     */
    private final UserRepository userRepository;

    /**
     * Маппер для преобразования между DTO и entity объектами.
     */
    private final UserMapper userMapper;

    /**
     * Создает нового пользователя в системе.
     * <p>
     * Перед сохранением проверяет уникальность email адреса.
     * Обрабатывает конфликты при сохранении.
     * </p>
     *
     * @param userDto DTO объект с данными для создания пользователя
     * @return UserDto созданный пользователь с присвоенным идентификатором
     * @throws DuplicateEmailException если пользователь с таким email уже существует
     * @throws DataIntegrityViolationException при конфликте целостности данных в БД
     *
     * @see UserService#create(UserDto)
     */
    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.debug("Получен запрос на создание пользователя с данными {}", userDto);
        existEmail(userDto.getEmail());
        try {
            User user = userRepository.save(userMapper.mapToModel(userDto));
            log.info("Пользователь успешно добавлен, id = {}", user.getId());
            return userMapper.mapToDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email уже занят (конфликт при сохранении)");
        }
    }

    /**
     * Обновляет данные существующего пользователя.
     * <p>
     * Выполняет частичное обновление данных пользователя. Если email изменяется,
     * проверяет его уникальность. Обрабатывает возможные конфликты при сохранении.
     * </p>
     *
     * @param userId идентификатор пользователя для обновления
     * @param updateUserDto DTO объект с данными для обновления
     * @return UserDto обновленный пользователь
     * @throws NotFoundException если пользователь с указанным id не найден
     * @throws DuplicateEmailException если новый email уже занят другим пользователем
     * @throws DataIntegrityViolationException при конфликте целостности данных в БД
     *
     * @see UserService#update(Integer, UpdateUserDto)
     */
    @Override
    @Transactional
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

    /**
     * Возвращает пользователя по идентификатору.
     * <p>
     * Выполняет поиск пользователя в базе данных и преобразует entity в DTO.
     * Выполняется в режиме "только для чтения" для оптимизации производительности.
     * </p>
     *
     * @param userId идентификатор запрашиваемого пользователя
     * @return UserDto данные пользователя
     * @throws NotFoundException если пользователь с указанным id не найден
     *
     * @see UserService#get(Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto get(Integer userId) {
        log.debug("Получен запрос на получение пользователя по id = {}", userId);
        log.info("Пользователь с id = {}, возвращен", userId);
        return userRepository.findById(userId)
                .map(userMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
    }

    /**
     * Удаляет пользователя из системы по идентификатору.
     * <p>
     * Выполняет атомарную операцию удаления с проверкой существования пользователя.
     * Если пользователь с указанным идентификатором не существует, выбрасывает исключение.
     * Операция выполняется в транзакции для обеспечения целостности данных.
     * </p>
     *
     * <p><b>Особенности реализации:</b></p>
     * <ul>
     *   <li>Использует оптимизированный запрос DELETE для минимизации обращений к БД</li>
     *   <li>Проверяет факт удаления через возвращаемое количество affected rows</li>
     *   <li>Гарантирует атомарность операции в рамках транзакции</li>
     * </ul>
     *
     * @param userId идентификатор пользователя для удаления, не должен быть null
     * @throws NotFoundException если пользователь с указанным id не найден
     * @throws IllegalArgumentException если userId равен null
     *
     * @see UserService#delete(Integer)
     * @see UserRepository#deleteUserById(Integer)
     */
    @Override
    @Transactional
    public void delete(Integer userId) {
        int deletedCount = userRepository.deleteUserById(userId);
        if (deletedCount == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        log.info("Пользователь с id = {} удален", userId);
    }

    /**
     * Вспомогательный метод.
     * Проверяет уникальность email адреса в системе.
     * <p>
     * Выполняет поиск в базе данных пользователя с указанным email.
     * Если пользователь с таким email существует, выбрасывает исключение.
     * </p>
     *
     * @param email email адрес для проверки
     * @throws DuplicateEmailException если пользователь с указанным email уже существует
     */
    private void existEmail(String email) {
        boolean hasEmail = userRepository.existsByEmail(email);
        if (hasEmail) {
            log.error("Пользователь с данным email = {}, уже существует!", email);
            throw new DuplicateEmailException("Пользователь с данным email уже существует");
        }
    }
}

