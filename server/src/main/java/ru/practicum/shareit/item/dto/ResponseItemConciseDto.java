package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseItemConciseDto {
    private String name;
    private String description;

}
