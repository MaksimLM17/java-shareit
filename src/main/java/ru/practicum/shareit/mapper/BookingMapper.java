package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
@Mapper(componentModel = "spring")
public interface BookingMapper {

    public Booking mapToModel(BookingDto bookingDto);

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    public BookingDto mapToDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", ignore = true)
    public Booking mapToModelFromRequest(BookingRequestDto bookingRequestDto);
}
