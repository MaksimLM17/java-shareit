package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime future = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(
                1,
                new ItemDto(1, "Перфоратор", "Ударный перфоратор", true, null, null),
                now,
                future,
                new UserDto(1, "Дмитрий", "Dmitriy1987@example.com"),
                Status.WAITING
        );

        bookingRequestDto = new BookingRequestDto(
                1,
                now,
                future
        );
    }

    @Test
    void add_shouldCreateBooking() throws Exception {
        when(bookingService.add(any(BookingRequestDto.class), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().name()));
    }

    @Test
    void approve_shouldUpdateBookingStatus() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.approve(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        when(bookingService.getById(anyInt(), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getAllBookingsCurrentUser_shouldReturnList() throws Exception {
        when(bookingService.getAllBookingsCurrentUser(anyInt(), anyString()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getAllBookingsItemsUser_shouldReturnList() throws Exception {
        when(bookingService.getAllBookingsItemsUser(anyInt(), anyString()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?state=FUTURE")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }
}