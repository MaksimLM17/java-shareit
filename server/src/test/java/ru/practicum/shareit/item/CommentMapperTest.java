package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void mapToDto_ShouldMapCommentToCommentDto() {
        User author = new User(1, "Author Name", "author@example.com");
        Item item = new Item(1, "Item Name", "Description", true, author, null);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Test comment");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));

        CommentDto dto = mapper.mapToDto(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getCreated(), dto.getCreated());

        assertNotNull(dto.getItem());
        assertEquals(comment.getItem().getId(), dto.getItem().getId());

        assertNotNull(dto.getAuthor());
        assertEquals(comment.getAuthor().getId(), dto.getAuthor().getId());
        assertEquals(comment.getAuthor().getName(), dto.getAuthor().getName());
    }

    @Test
    void mapToDto_ShouldHandleNull() {
        assertNull(mapper.mapToDto(null));
    }

    @Test
    void mapToDto_ShouldHandleNullFields() {
        // Given
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = mapper.mapToDto(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertNull(dto.getItem());
        assertNull(dto.getAuthor());
    }
}
