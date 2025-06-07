package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.CommonUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemRepo implements ItemRepository {

    private final Map<Integer, Item> items;

    @Override
    public Item create(Integer userId, Item item) {
        Integer id = CommonUtils.getNextId(items);
        item.setId(id);
        log.debug("Сгенерирован новый id = {}, для новой вещи", id);
        item.setOwner(userId);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Integer itemId, UpdateItem item) {
        Item existingItem = items.get(itemId);
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, existingItem);
        return existingItem;
    }

    @Override
    public Item getById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUser(Integer userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .toList();
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .toList();
    }

    @Override
    public boolean checkItem(Integer itemId) {
        return items.containsKey(itemId);
    }

    @Override
    public boolean checkUserForItem(Integer userId, Integer itemId) {
        return Objects.equals(items.get(itemId).getOwner(), userId);
    }
}
