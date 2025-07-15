package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void toFullDto_ShouldMapCorrectly() {
        User requestor = new User(1, "User", "user@example.com");
        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setDescription("Need item");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));

        ItemRequestFullDto dto = mapper.mapToDto(request);

        assertNotNull(dto);
        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
    }

    @Test
    void toFullDto_ShouldHandleNull() {
        assertNull(mapper.mapToDto(null));
    }
}
