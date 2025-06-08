package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Integer userId, Item item);

    Item update(Integer itemId, UpdateItemDto item);

    Item getById(Integer itemId);

    List<Item> getItemsByUser(Integer userId);

    List<Item> searchItems(String text);

    boolean checkItem(Integer itemId);

    boolean checkUserForItem(Integer userId, Integer itemId);
}
