package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto add(BookingRequestDto bookingRequestDto, Integer userId) {
        log.debug("Получен запрос на создание брони пользователем с id = {}, " +
                "для вещи с id = {}", userId, bookingRequestDto.getItemId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingRequestDto.getItemId() + " не найдена!"));

        if (item.getOwner().getId().equals(userId)) {
            log.error("Попытка забронировать свою вещь!");
            throw new BadRequestException("Нельзя бронировать свою же вещь");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            log.error("Указаны некорректные даты начала и конца бронирования: дата начала = {}, дата конца = {}",
                    bookingRequestDto.getStart(), bookingRequestDto.getEnd());
            throw new BadRequestException("Дата начала должна быть раньше даты окончания");
        }

        Booking booking = bookingMapper.mapToModelFromRequest(bookingRequestDto);
        if (item.isAvailable()) {
            booking.setItem(item);
            booking.setBooker(user);
            booking.setStatus(Status.WAITING);
            Booking savedBooking = bookingRepository.save(booking);
            log.info("Вещь с id = {}, забронирована!", item.getId());
            return bookingMapper.mapToDto(savedBooking);
        } else {
            log.error("Вещь с id = {}, недоступна для бронирования", item.getId());
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }

    @Override
    public BookingDto approve(Integer bookingId, Integer userId, boolean approved) {
        log.info("Получен запрос на обновление статуса брони");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено!"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new BadRequestException("Обновление статуса бронирования доступно только владельцам вещи!");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new BadRequestException("Нельзя изменить статус уже подтверждённого бронирования");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
            log.info("Бронирование подтверждено!");
            return bookingMapper.mapToDto(booking);
        } else {
            booking.setStatus(Status.REJECTED);
            bookingRepository.save(booking);
            log.info("Бронирование отклонено!");
            return bookingMapper.mapToDto(booking);
        }
    }

    @Override
    public BookingDto getById(Integer bookingId, Integer userId) {
        log.debug("Получен запрос на просмотр бронирования по id = {}", bookingId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено!"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) &&
                !Objects.equals(booking.getBooker().getId(), userId)) {
            log.error("Попытка посмотреть детали бронирования пользователем с id = {}, " +
                    "который не является автором бронирования и владельцем вещи!", userId);
            throw new BadRequestException("Пользователь с id " + userId + " не является владельцем вещи или автором бронирования!");
        }
        return bookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsCurrentUser(Integer userId, String state) {
        log.debug("Получен запрос на получение всех бронирований пользователя {}, со статусом {}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        validateState(state);
        List<Booking> bookings = bookingRepository.getAllBookingsByUserId(userId, state);
        log.debug("Отправлен список бронирований пользователя размером {}", bookings.size());
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllBookingsItemsUser(Integer userId, String state) {
        log.debug("Получен запрос на получение всех бронирований для вещей пользователя {}, со статусом {}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден!"));
        validateState(state);
        List<Booking> bookings = bookingRepository.getAllBookingsItemsUser(userId, state);
        log.debug("Отправлен список всех бронирований вещей пользователя размером {}", bookings.size());
        return bookings.stream()
                .map(bookingMapper::mapToDto)
                .toList();
    }

    private void validateState(String state) {
        if (!List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED").contains(state)) {
            log.error("Передано неизвестное значение статуса = {}", state);
            throw new BadRequestException("Неизвестное значение статуса: " + state);
        }
    }
}
