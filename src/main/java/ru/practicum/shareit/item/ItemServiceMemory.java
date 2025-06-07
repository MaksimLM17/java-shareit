package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemConcise;
import ru.practicum.shareit.item.dto.UpdateItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceMemory implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        log.debug("Получен запрос на добавление вещи пользователем {}, данные вещи {}", userId, itemDto);
        if (!userRepository.checkUser(userId)) {
            log.error("Пользователь с id {} не найден! При запросе на добавление новой вещи", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        Item item = itemRepository.create(userId, ItemMapper.mapToModel(itemDto));
        log.info("Вещь создана: {}", item);
        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemDto update(Integer userId, Integer itemId, UpdateItem updateItem) {
        log.debug("Получен запрос на обновление вещи пользователем {}, данные вещи {}", userId, updateItem);
        checksForUpdate(userId, itemId);
        log.info("Вещь {}, обновлена", itemId);
        return ItemMapper.mapToDto(itemRepository.update(itemId, updateItem));
    }

    @Override
    public ItemDto getById(Integer itemId) {
        if (!itemRepository.checkItem(itemId)) {
            log.error("Вещь с id {} не найдена! При запросе на получение вещи по id", itemId);
            throw new NotFoundException("Вещь с id " + itemId + " не найдена!");
        }
        return ItemMapper.mapToDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ResponseItemConcise> getItemsForUser(Integer userId) {
        if (!userRepository.checkUser(userId)) {
            log.error("Пользователь с id {} не найден! При запросе на получение вещей пользователя", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        return itemRepository.getItemsByUser(userId).stream()
                .map(ItemMapper::mapToResponseConcise)
                .toList();
    }

    @Override
    public List<ResponseItemConcise> searchItems(String text) {
        log.debug("Получен запрос: поиск вещей по имени или описанию!");
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::mapToResponseConcise)
                .toList();
    }

    private void checksForUpdate(Integer userId, Integer itemId) {
        if (!userRepository.checkUser(userId)) {
            log.error("Пользователь с id {} не найден! При запросе на обновление вещи", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
        if (!itemRepository.checkItem(itemId)) {
            log.error("Вещь с id {} не найдена! При запросе на обновление вещи по id", itemId);
            throw new NotFoundException("Вещь с id " + itemId + " не найдена!");
        }
        if (!itemRepository.checkUserForItem(userId, itemId)) {
            log.error("Вещь с id {}, добавлена другим пользователем, обновление доступно только владельцам!", itemId);
            throw new NotFoundException("Вещь с id " + itemId + " добавлена другим пользователем, обновление доступно только владельцам!");
        }
    }
}
