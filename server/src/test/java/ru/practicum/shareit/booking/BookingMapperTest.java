package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void mapToModel_ShouldMapBookingDtoToBooking() {
        BookingDto dto = new BookingDto();
        dto.setId(1);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setStatus(Status.WAITING);

        Item item = new Item();
        item.setId(10);
        dto.setItem(new ItemDto());

        User booker = new User();
        booker.setId(20);
        dto.setBooker(new UserDto(booker.getId(), booker.getName(), booker.getEmail()));

        Booking booking = mapper.mapToModel(dto);

        assertNotNull(booking);
        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(dto.getStatus(), booking.getStatus());
        assertNotNull(booking.getItem());
        assertEquals(dto.getItem().getId(), booking.getItem().getId());
        assertNotNull(booking.getBooker());
        assertEquals(dto.getBooker().getId(), booking.getBooker().getId());
    }

    @Test
    void mapToDto_ShouldMapBookingToBookingDto() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.APPROVED);

        Item item = new Item();
        item.setId(10);
        booking.setItem(item);

        User booker = new User();
        booker.setId(20);
        booking.setBooker(booker);

        BookingDto dto = mapper.mapToDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertNotNull(dto.getItem());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertNotNull(dto.getBooker());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
    }

    @Test
    void mapToModelFromRequest_ShouldMapBookingRequestDtoToBooking_IgnoringFields() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now());
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = mapper.mapToModelFromRequest(requestDto);

        assertNotNull(booking);
        assertNull(booking.getId());
        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertNull(booking.getStatus());
    }

    @Test
    void mapToModelFromRequest_ShouldHandleNull() {
        assertNull(mapper.mapToModelFromRequest(null));
    }

    @Test
    void mapToModel_ShouldHandleNull() {
        assertNull(mapper.mapToModel(null));
    }

    @Test
    void mapToDto_ShouldHandleNull() {
        assertNull(mapper.mapToDto(null));
    }
}
