package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentRequestDto;
import ru.practicum.shareit.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId,Integer itemId, UpdateItemDto updateItemDto);

    ItemWithBookingDto getById(Integer userId, Integer itemId);

    List<ResponseItemConciseDto> getItemsForUser(Integer userId, Integer from, Integer size);

    List<ResponseItemConciseDto> searchItems(String text);

    CommentResponseDto createComment(CommentRequestDto commentRequestDto, Integer itemId, Integer userId);

}
