package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.annotation.NotBlankIfPresent;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {

    @NotBlankIfPresent(message = "Имя вещи не может быть передано пустым (Обновление вещи)")
    private String name;

    @NotBlankIfPresent(message = "Описание вещи не может быть передано пустым (Обновление вещи)")
    private String description;

    private Boolean available;
}
