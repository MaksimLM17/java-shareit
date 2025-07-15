package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private ItemRequestService requestService;

    private ItemRequestInDto requestInDto;
    private ItemRequestFullDto requestFullDto;
    private ItemRequestWithResponsesDto requestWithResponsesDto;

    @BeforeEach
    void setUp() {
        requestInDto = new ItemRequestInDto();
        requestInDto.setDescription("Срочно нужен лобзик");

        requestFullDto = new ItemRequestFullDto();
        requestFullDto.setId(1);
        requestFullDto.setDescription("Срочно нужен лобзик");
        requestFullDto.setCreated(LocalDateTime.now());

        requestWithResponsesDto = new ItemRequestWithResponsesDto();
        requestWithResponsesDto.setId(1);
        requestWithResponsesDto.setDescription("Срочно нужен лобзик");
        requestWithResponsesDto.setCreated(LocalDateTime.now());
    }

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        when(requestService.create(anyInt(), any(ItemRequestInDto.class))).thenReturn(requestFullDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestFullDto.getId()))
                .andExpect(jsonPath("$.description").value(requestFullDto.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getAllOwn_shouldReturnUserRequests() throws Exception {
        when(requestService.getAllOwn(anyInt())).thenReturn(List.of(requestWithResponsesDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestWithResponsesDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestWithResponsesDto.getDescription()))
                .andExpect(jsonPath("$[0].items").isArray());
    }

    @Test
    void getAllOthers_shouldReturnOtherUsersRequests() throws Exception {
        when(requestService.getAllOthers(anyInt())).thenReturn(List.of(requestFullDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestFullDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestFullDto.getDescription()));
    }

    @Test
    void getById_shouldReturnRequest() throws Exception {
        when(requestService.getById(anyInt())).thenReturn(requestWithResponsesDto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestWithResponsesDto.getId()))
                .andExpect(jsonPath("$.description").value(requestWithResponsesDto.getDescription()))
                .andExpect(jsonPath("$.items").isArray());
    }
}