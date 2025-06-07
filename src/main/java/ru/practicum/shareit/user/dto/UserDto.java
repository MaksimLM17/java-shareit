package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "Имя пользователя не может быть пробелом")
    @NotNull(message = "name, обязательное поле для заполнения")
    private String name;
    @NotNull(message = "Email, обязательное поле для заполнения")
    @Email(message = "Некорректный email")
    private String email;

}
