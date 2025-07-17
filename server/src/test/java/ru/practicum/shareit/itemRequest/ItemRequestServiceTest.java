package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper requestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final Integer userId = 1;
    private final Integer requestId = 1;
    private final ItemRequestInDto requestInDto = new ItemRequestInDto("Нужен ледобур");
    private final User user = new User();
    private final ItemRequest itemRequest = new ItemRequest();
    private final ItemRequestFullDto requestFullDto = new ItemRequestFullDto(1, "Нужен ледобур", LocalDateTime.now());
    private final ItemRequestWithResponsesDto withResponsesDto = new ItemRequestWithResponsesDto();

    @Test
    void create_shouldSaveNewRequest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(requestMapper.mapToDto(itemRequest)).thenReturn(requestFullDto);

        ItemRequestFullDto result = itemRequestService.create(userId, requestInDto);

        assertNotNull(result);
        assertEquals(requestFullDto.getId(), result.getId());
        verify(userRepository).findById(userId);
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(userId, requestInDto));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getAllOwn_shouldReturnRequestsWithResponses() {
        itemRequest.setId(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(1)).thenReturn(List.of());

        List<ItemRequestWithResponsesDto> result = itemRequestService.getAllOwn(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository).findById(userId);
        verify(requestRepository).findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    @Test
    void getAllOwn_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOwn(userId));
        verify(requestRepository, never()).findAllByRequesterIdOrderByCreatedDesc(anyInt());
    }

    @Test
    void getAllOthers_shouldReturnOtherUsersRequests() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));
        when(requestMapper.mapToDto(itemRequest)).thenReturn(requestFullDto);

        List<ItemRequestFullDto> result = itemRequestService.getAllOthers(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository).findById(userId);
        verify(requestRepository).findAllByRequesterIdNotOrderByCreatedDesc(userId);
    }

    @Test
    void getAllOthers_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOthers(userId));
        verify(requestRepository, never()).findAllByRequesterIdNotOrderByCreatedDesc(anyInt());
    }

    @Test
    void getById_shouldReturnRequestWithResponses() {
        itemRequest.setId(requestId);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of());

        ItemRequestWithResponsesDto result = itemRequestService.getById(requestId);

        assertNotNull(result);
        verify(requestRepository).findById(requestId);
        verify(itemRepository).findAllByRequestId(requestId);
    }

    @Test
    void getById_shouldThrowExceptionWhenRequestNotFound() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestId));
        verify(itemRepository, never()).findAllByRequestId(anyInt());
    }
}