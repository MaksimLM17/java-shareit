package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUser;

public interface UserRepository {

    User create(User user);

    User update(Integer userId, UpdateUser user);

    User get(Integer userId);

    void delete(Integer userId);

    User getById(Integer userId);

    boolean checkUser(Integer userId);

    boolean existsByEmail(String email);
}
