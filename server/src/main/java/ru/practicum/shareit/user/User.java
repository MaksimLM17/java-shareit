package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Класс представляет сущность пользователя в системе.
 * <p>
 * Пользователь имеет уникальный идентификатор, имя и email.
 * Email должен быть уникальным в системе.
 * </p>
 *
 * @author MaksimLM17
 * @version 1.0
 * @see UserRepository
 * @see UserService
 * @since 2025
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    /**
     * Уникальный идентификатор пользователя.
     * <p>
     * Генерируется автоматически базой данных при создании новой записи.
     * Не может быть null.
     * </p>
     *
     * @see GenerationType#IDENTITY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    /**
     * Имя пользователя.
     * <p>
     * Не может быть null или пустым.
     * Максимальная длина - 127 символов.
     * </p>
     */
    private String name;
    /**
     * Email адрес электронной почты.
     * <p>
     * Должен быть уникальным в системе.
     * Не может быть null или пустым.
     * Должен соответствовать формату email адреса.
     * Максимальная длина - 127 символов.
     * </p>
     */
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
