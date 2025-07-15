package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void mapToDto_withRequest_shouldMapCorrectly() {
        User owner = new User(1, "Owner", "owner@example.com");
        ItemRequest request = new ItemRequest();
        request.setId(10);
        Item item = new Item(1, "Item", "Description", true, owner, request);

        ItemDto result = ItemMapper.mapToDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.getAvailable());
        assertEquals(item.getOwner(), result.getOwner());
        assertEquals(request.getId(), result.getRequestId());
    }

    @Test
    void mapToDto_withoutRequest_shouldMapCorrectly() {
        User owner = new User(1, "Owner", "owner@example.com");
        Item item = new Item(1, "Item", "Description", true, owner, null);

        ItemDto result = ItemMapper.mapToDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.getAvailable());
        assertEquals(item.getOwner(), result.getOwner());
        assertNull(result.getRequestId());
    }

    @Test
    void mapToModel_shouldMapCorrectly() {
        User owner = new User(1, "Owner", "owner@example.com");
        ItemDto itemDto = new ItemDto(1, "Item", "Description", true, owner, 10);

        Item result = ItemMapper.mapToModel(itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.isAvailable());
        assertEquals(itemDto.getOwner(), result.getOwner());
        assertNull(result.getId());
        assertNull(result.getRequest());
    }

    @Test
    void mapToResponseConcise_shouldMapCorrectly() {
        Item item = new Item(1, "Item", "Description", true, null, null);

        ResponseItemConciseDto result = ItemMapper.mapToResponseConcise(item);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    void mapToModelFromUpdatedItem_shouldUpdateFields() {
        Item originalItem = new Item(1, "Original", "Original description", false, null, null);
        UpdateItemDto updateDto = new UpdateItemDto("Updated", "Updated description", true);

        Item result = ItemMapper.mapToModelFromUpdatedItem(updateDto, originalItem);

        assertSame(originalItem, result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(updateDto.getAvailable(), result.isAvailable());
    }

    @Test
    void mapToModelFromUpdatedItem_withNullFields_shouldNotUpdate() {
        Item originalItem = new Item(1, "Original", "Original description", false, null, null);
        UpdateItemDto updateDto = new UpdateItemDto(null, null, null);

        Item result = ItemMapper.mapToModelFromUpdatedItem(updateDto, originalItem);

        assertSame(originalItem, result);
        assertEquals("Original", result.getName());
        assertEquals("Original description", result.getDescription());
        assertFalse(result.isAvailable());
    }

    @Test
    void mapToItemWithBooking_shouldMapCorrectly() {
        Item item = new Item(1, "Item", "Description", true, null, null);
        BookingDto lastBooking = new BookingDto();
        BookingDto nextBooking = new BookingDto();
        List<CommentDto> comments = List.of(new CommentDto());

        ItemWithBookingDto result = ItemMapper.mapToItemWithBooking(item, lastBooking, nextBooking, comments);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.getAvailable());
        assertSame(lastBooking, result.getLastBooking());
        assertSame(nextBooking, result.getNextBooking());
        assertSame(comments, result.getComments());
    }
}
