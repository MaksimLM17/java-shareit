package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponseInRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithResponsesDto {

    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseInRequestDto> items = new ArrayList<>();

}
