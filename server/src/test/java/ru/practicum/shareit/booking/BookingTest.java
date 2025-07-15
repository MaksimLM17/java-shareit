package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testNoArgsConstructor() {
        Booking booking = new Booking();

        assertNull(booking.getId());
        assertNull(booking.getStart());
        assertNull(booking.getEnd());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertNull(booking.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Item item = new Item();
        User booker = new User();

        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);

        assertEquals(1, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertSame(item, booking.getItem());
        assertSame(booker, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        Booking booking = new Booking();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Item item = new Item();
        User booker = new User();

        booking.setId(1);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        assertEquals(1, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertSame(item, booking.getItem());
        assertSame(booker, booking.getBooker());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1);

        Booking booking2 = new Booking();
        booking2.setId(1);

        Booking booking3 = new Booking();
        booking3.setId(2);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        Booking booking = new Booking();
        booking.setId(1);

        assertNotEquals(null, booking);
    }

    @Test
    void testToString() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStatus(Status.WAITING);

        String toStringResult = booking.toString();

        assertTrue(toStringResult.contains("Booking"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("status=WAITING"));
    }

    @Test
    void testStatusEnum() {
        Status waiting = Status.WAITING;
        Status approved = Status.APPROVED;
        Status rejected = Status.REJECTED;
        Status cancelled = Status.CANCELLED;

        assertEquals("WAITING", waiting.name());
        assertEquals("APPROVED", approved.name());
        assertEquals("REJECTED", rejected.name());
        assertEquals("CANCELLED", cancelled.name());
    }
}
