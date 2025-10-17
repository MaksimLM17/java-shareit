package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestFullDto create(Integer userId, ItemRequestInDto itemRequestInDto);

    List<ItemRequestWithResponsesDto> getAllOwn(Integer userId);

    List<ItemRequestFullDto> getAllOthers(Integer userId);

    ItemRequestWithResponsesDto getById(Integer requestId);
}
