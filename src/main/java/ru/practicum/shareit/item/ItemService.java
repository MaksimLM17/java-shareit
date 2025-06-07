package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConcise;
import ru.practicum.shareit.item.dto.UpdateItem;

import java.util.List;

public interface ItemService {

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId,Integer itemId, UpdateItem updateItem);

    ItemDto getById(Integer itemId);

    List<ResponseItemConcise> getItemsForUser(Integer userId);

    List<ResponseItemConcise> searchItems(String text);

}
