package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeToJson() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1)
                .start(LocalDateTime.of(2023, 10, 1, 12, 0))
                .end(LocalDateTime.of(2023, 10, 2, 12, 0))
                .status(Status.APPROVED)
                .item(new ItemDto(1, "Блендер", "Погружной", true, null, null))
                .booker(new UserDto(1, "Мария", "mashaEkb@example.com"))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2023-10-01T12:00:00\"");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"name\":\"Блендер\"");
    }

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String json = "{\"id\":1,\"start\":\"2023-10-01T12:00:00\",\"end\":\"2023-10-02T12:00:00\"," +
                "\"status\":\"APPROVED\",\"item\":{\"id\":1,\"name\":\"Блендер\",\"description\":\"Погружной\"," +
                "\"available\":true},\"booker\":{\"id\":1,\"name\":\"Мария\",\"email\":\"mashaEkb@example.com\"}}";

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 12, 0));
        assertThat(dto.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(dto.getItem().getName()).isEqualTo("Блендер");
    }
}
