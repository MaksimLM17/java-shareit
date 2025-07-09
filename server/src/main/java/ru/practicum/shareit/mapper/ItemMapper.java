package ru.practicum.shareit.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto mapToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getOwner());
    }

    public static Item mapToModel(ItemDto itemDto) {
        return new Item(itemDto.getName(), itemDto.getDescription(),itemDto.getAvailable(), itemDto.getOwner());
    }

    public static ResponseItemConciseDto mapToResponseConcise(Item item) {
        return new ResponseItemConciseDto(item.getName(), item.getDescription());
    }

    public static Item mapToModelFromUpdatedItem(UpdateItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    public static ItemWithBookingDto mapToItemWithBooking(Item item,
                                                          BookingDto lastBooking,
                                                          BookingDto nextBooking,
                                                          List<CommentDto> comments) {
        return new ItemWithBookingDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(),
                lastBooking, nextBooking, comments);
    }

}
