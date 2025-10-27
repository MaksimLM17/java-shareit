package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя для удаления
     * @return количество удаленных записей (0 или 1)
     */
    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :userId")
    int deleteUserById(@Param("userId") Integer userId);

    /**
     * Проверяет существование пользователя с указанным email адресом.
     * <p>
     * Используется для валидации уникальности email при создании и обновлении
     * пользователей. Метод автоматически генерируется Spring Data JPA
     * по соглашению об именовании.
     * </p>
     *
     * @param email email адрес для проверки, не должен быть null или пустым
     * @return true если пользователь с указанным email существует, false в противном случае
     *
     */
    boolean existsByEmail(String email);
}
