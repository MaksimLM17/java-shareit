package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.annotation.NotBlankIfPresent;

@Data
public class UpdateItemDto {
    @NotBlankIfPresent
    private String name;
    @NotBlankIfPresent
    private String description;
    private Boolean available;
}
