package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemModelTest {

    @Test
    void testEqualsAndHashCode() {
        User owner = new User(1, "Owner", "owner@example.com");

        Item item1 = new Item(1, "Item 1", "Description 1", true, owner, null);
        Item item2 = new Item(1, "Item 2", "Description 2", false, null, null);
        Item item3 = new Item(3, "Item 1", "Description 1", true, owner, null);

        assertEquals(item1, item2, "Объекты с одинаковым id должны быть равны");
        assertNotEquals(item1, item3, "Объекты с разным id не должны быть равны");

        assertEquals(item1.hashCode(), item2.hashCode(), "Хэш-коды объектов с одинаковым id должны совпадать");
        assertNotEquals(item1.hashCode(), item3.hashCode(), "Хэш-коды объектов с разным id должны отличаться");
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();

        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
        assertFalse(item.isAvailable());
    }

    @Test
    void testAllArgsConstructor() {
        User owner = new User(1, "Owner", "owner@example.com");
        ItemRequest request = new ItemRequest(1, "Need item", null, null);

        Item item = new Item(1, "Drill", "Powerful drill", true, owner, request);

        assertEquals(1, item.getId());
        assertEquals("Drill", item.getName());
        assertEquals("Powerful drill", item.getDescription());
        assertTrue(item.isAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testConstructorWithoutRequest() {
        User owner = new User(1, "Owner", "owner@example.com");

        Item item = new Item("Hammer", "Heavy hammer", false, owner);

        assertNull(item.getId());
        assertEquals("Hammer", item.getName());
        assertEquals("Heavy hammer", item.getDescription());
        assertFalse(item.isAvailable());
        assertEquals(owner, item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void testSettersAndGetters() {
        Item item = new Item();
        User owner = new User(1, "Owner", "owner@example.com");
        ItemRequest request = new ItemRequest(1, "Need item", null, null);

        item.setId(1);
        item.setName("Saw");
        item.setDescription("Circular saw");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        assertEquals(1, item.getId());
        assertEquals("Saw", item.getName());
        assertEquals("Circular saw", item.getDescription());
        assertTrue(item.isAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testToString() {
        User owner = new User(1, "Owner", "owner@example.com");
        Item item = new Item(1, "Laptop", "Gaming laptop", true, owner, null);

        String expectedString = "Item(id=1, name=Laptop, description=Gaming laptop, available=true, owner=User(id=1, name=Owner, email=owner@example.com), request=null)";
        assertEquals(expectedString, item.toString());
    }
}
