package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentRequestDto;
import ru.practicum.shareit.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemConciseDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemWithBookingDto itemWithBookingDto;
    private ResponseItemConciseDto responseItemConciseDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1, "Перфоратор", "Ударный", true, null, null);

        itemWithBookingDto = new ItemWithBookingDto(1, "Перфоратор",
                "Ударный", true, null, null, Collections.emptyList());

        responseItemConciseDto = new ResponseItemConciseDto("Перфоратор", "Ударный");

        commentResponseDto = new CommentResponseDto(1, "Отличный инструмент!",
                1,"John", LocalDateTime.now());
    }

    @Test
    void create_shouldReturnItemDto() throws Exception {
        when(itemService.create(anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("Ударный перфоратор");
        updateItemDto.setDescription("Большое количество буров и приспособлений");
        updateItemDto.setAvailable(false);

        when(itemService.update(anyInt(), anyInt(), any(UpdateItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void getById_shouldReturnItemWithBookings() throws Exception {
        when(itemService.getById(anyInt(), anyInt())).thenReturn(itemWithBookingDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemWithBookingDto.getId()))
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking").doesNotExist());
    }

    @Test
    void getItemsForUser_shouldReturnListOfItems() throws Exception {
        when(itemService.getItemsForUser(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseItemConciseDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(responseItemConciseDto.getName()))
                .andExpect(jsonPath("$[0].description").value(responseItemConciseDto.getDescription()));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        when(itemService.searchItems(anyString()))
                .thenReturn(List.of(responseItemConciseDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(responseItemConciseDto.getName()));
    }

    @Test
    void createComment_shouldReturnCreatedComment() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Отличный инструмент!");

        when(itemService.createComment(any(CommentRequestDto.class), anyInt(), anyInt()))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()));
    }
}