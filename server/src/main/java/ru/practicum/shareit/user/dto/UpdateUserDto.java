package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.annotation.NotBlankIfPresent;

@Data
public class UpdateUserDto {

    private String name;
    private String email;
}
