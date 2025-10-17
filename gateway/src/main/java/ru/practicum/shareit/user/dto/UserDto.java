package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "Имя пользователя не может быть пробелом")
    private String name;

    @NotBlank(message = "Email, обязательное поле для заполнения")
    @Email(message = "Некорректный email")
    private String email;
}
