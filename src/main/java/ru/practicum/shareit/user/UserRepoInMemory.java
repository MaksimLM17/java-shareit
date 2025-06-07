package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UpdateUser;
import ru.practicum.shareit.util.CommonUtils;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepoInMemory implements UserRepository {

    private final Map<Integer, User> users;


    @Override
    public User create(User user) {
        Integer newId = CommonUtils.getNextId(users);
        user.setId(newId);
        log.debug("Сгенерирован новый id = {}, для нового пользователя", newId);
        users.put(newId, user);
        return user;
    }

    @Override
    public User update(Integer userId, UpdateUser user) {
        User existingUser = users.get(userId);
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        users.put(userId, existingUser);
        return existingUser;
    }

    @Override
    public User get(Integer userId) {
        return users.get(userId);
    }

    @Override
    public void delete(Integer userId) {
        users.remove(userId);
    }

    @Override
    public User getById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public boolean checkUser(Integer userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
