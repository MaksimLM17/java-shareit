package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseInRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    public ItemRequestFullDto create(Integer userId, ItemRequestInDto itemRequestInDto) {
        log.debug("Получен запрос на создание реквеста пользователем {}, данные запроса {}", userId, itemRequestInDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден, при создании запроса!"));

        ItemRequest itemRequest = new ItemRequest(itemRequestInDto.getDescription());
        itemRequest.setRequester(user);
        ItemRequest savedItemRequest = requestRepository.save(itemRequest);
        log.info("Запрос создан с данными: {}", savedItemRequest);
        return requestMapper.mapToDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestWithResponsesDto> getAllOwn(Integer userId) {
        log.debug("Получен запрос на получение списка реквестов с ответами, пользователем {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                        " не найден, при получении своих запросов с ответами вещи!"));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        log.info("Отправлен список размером: {}", requests.size());
        return requests.stream()
                .map(this::mapToResponses)
                .toList();
    }

    @Override
    public List<ItemRequestFullDto> getAllOthers(Integer userId) {
        log.debug("Получен запрос на получение списка реквестов созданных другими людьми, пользователем {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                        " не найден, при получении запросов других пользователей!"));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        log.info("Отправлен список размером: {}", requests.size());
        return requests.stream()
                .map(requestMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemRequestWithResponsesDto getById(Integer requestId) {
        log.debug("Получен запрос на получение  реквеста по id: {}", requestId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId +
                        " не найден!"));
        return mapToResponses(itemRequest);
    }

    private ItemRequestWithResponsesDto mapToResponses(ItemRequest itemRequest) {
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        log.info("Получен список ответов на запрос: {}", items);
        List<ItemResponseInRequestDto> responses = items.stream()
                .map(ItemMapper::mapToResponseForItemRequest)
                .toList();

        return new ItemRequestWithResponsesDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                responses
        );
    }
}
