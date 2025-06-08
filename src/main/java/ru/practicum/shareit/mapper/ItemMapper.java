package ru.practicum.shareit.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto mapToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static Item mapToModel(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(),itemDto.getAvailable());
    }

    public static ResponseItemConciseDto mapToResponseConcise(Item item) {
        return new ResponseItemConciseDto(item.getName(), item.getDescription());
    }

}
