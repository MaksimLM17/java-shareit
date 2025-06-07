package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.annotation.NotBlankIfPresent;

@Data
public class UpdateUser {
    @NotBlankIfPresent
    private String name;
    @Email
    private String email;
}
