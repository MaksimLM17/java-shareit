package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void testEntityMapping() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        em.persist(user);

        ItemRequest request = new ItemRequest("Нужен гриль");
        request.setRequestor(user);

        ItemRequest savedRequest = em.persistAndFlush(request);

        assertNotNull(savedRequest.getId());
        assertEquals("Нужен гриль", savedRequest.getDescription());
        assertNotNull(savedRequest.getCreated());
        assertEquals(user.getId(), savedRequest.getRequestor().getId());
    }

    @Test
    void testEqualsAndHashCode() {
        ItemRequest request1 = new ItemRequest(1, "Request 1", null, null);
        ItemRequest request2 = new ItemRequest(1, "Request 2", null, null);
        ItemRequest request3 = new ItemRequest(3, "Request 3", null, null);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testPrePersist() {
        ItemRequest request = new ItemRequest("Test description");
        assertNull(request.getCreated());

        em.persistAndFlush(request);
        assertNotNull(request.getCreated());
        assertTrue(request.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testConstructor() {
        ItemRequest request = new ItemRequest("Test description");
        assertNull(request.getId());
        assertEquals("Test description", request.getDescription());
        assertNull(request.getRequestor());
        assertNull(request.getCreated());
    }

    @Test
    void testToString() {
        ItemRequest request = new ItemRequest(1, "Test", null, LocalDateTime.now());
        assertThat(request.toString())
                .contains("id=1")
                .contains("description=Test");
    }

    @Test
    void testLazyLoading() {
        User user = new User();
        user.setName("Test User");
        em.persist(user);

        ItemRequest request = new ItemRequest("Test");
        request.setRequestor(user);
        em.persistAndFlush(request);
        em.clear();

        ItemRequest foundRequest = em.find(ItemRequest.class, request.getId());
        assertNotNull(foundRequest.getRequestor());
    }
}
