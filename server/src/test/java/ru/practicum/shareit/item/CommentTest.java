package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testNoArgsConstructor() {
        Comment comment = new Comment();

        assertNull(comment.getId());
        assertNull(comment.getText());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
        assertNull(comment.getCreated());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User author = new User();
        Item item = new Item();

        Comment comment = new Comment(1, "Test comment", item, author, now);

        assertEquals(1, comment.getId());
        assertEquals("Test comment", comment.getText());
        assertSame(item, comment.getItem());
        assertSame(author, comment.getAuthor());
        assertEquals(now, comment.getCreated());
    }

    @Test
    void testRequiredArgsConstructor() {
        User author = new User();
        Item item = new Item();

        Comment comment = new Comment("Test comment", item, author);

        assertNull(comment.getId());
        assertEquals("Test comment", comment.getText());
        assertSame(item, comment.getItem());
        assertSame(author, comment.getAuthor());
        assertNull(comment.getCreated());
    }

    @Test
    void testPrePersist() {
        Comment comment = new Comment();

        comment.onCreate();

        assertNotNull(comment.getCreated());
        assertTrue(comment.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                comment.getCreated().isEqual(LocalDateTime.now()));
    }

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1);

        Comment comment2 = new Comment();
        comment2.setId(1);

        Comment comment3 = new Comment();
        comment3.setId(2);

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }


    @Test
    void testToString() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("Test");

        String toStringResult = comment.toString();

        assertTrue(toStringResult.contains("Comment"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("text=Test"));
    }
}
