package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.annotation.NotBlankIfPresent;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    @NotBlankIfPresent(message = "поле name не может быть пустым или содержать только пробелы (Обновление пользователя)")
    private String name;

    @Email(message = "Некорректный email (Обновление пользователя)")
    private String email;
}
