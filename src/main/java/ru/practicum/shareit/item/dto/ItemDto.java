package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(message = "поле name не может быть пустым или содержать только пробелы")
    private String name;
    @NotBlank(message = "поле description не может быть пустым или содержать только пробелы")
    private String description;
    @NotNull(message = "Статус доступности вещи должен быть указан")
    private Boolean available;
    @JsonIgnore
    private User owner;
}
