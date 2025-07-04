package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "item", source = "item")
    @Mapping(target = "author", source = "author")
    public CommentDto mapToDto(Comment comment);
}
