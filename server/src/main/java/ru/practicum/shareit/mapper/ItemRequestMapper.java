package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {


    public ItemRequestFullDto mapToDto(ItemRequest itemRequest);

}
