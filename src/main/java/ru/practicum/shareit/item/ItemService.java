package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId,Integer itemId, UpdateItemDto updateItemDto);

    ItemDto getById(Integer itemId);

    List<ResponseItemConciseDto> getItemsForUser(Integer userId);

    List<ResponseItemConciseDto> searchItems(String text);

}
