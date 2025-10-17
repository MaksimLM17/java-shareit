package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND (:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsByUserId(@Param("userId") Integer userId, @Param("state") String state);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner.id = :userId " +
            "AND (:state = 'ALL' OR " +
            "(:state = 'CURRENT' AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP) OR " +
            "(:state = 'PAST' AND b.end < CURRENT_TIMESTAMP) OR " +
            "(:state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP) OR " +
            "(:state = 'WAITING' AND b.status = 'WAITING') OR " +
            "(:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsItemsUser(@Param("userId") Integer userId, @Param("state") String state);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end < :currentTime " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Booking findByItemIdLastBooking(@Param("itemId") Integer itemId,
                                    @Param("status") Status status,
                                    @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start " +
            "LIMIT 1")
    Booking findByItemIdNextBooking(@Param("itemId") Integer itemId,
                                    @Param("status") Status status,
                                    @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end <= :currentTime")
    boolean existsApprovedPastBookingForItem(
            @Param("userId") Integer userId,
            @Param("itemId") Integer itemId,
            @Param("status") Status status,
            @Param("currentTime") LocalDateTime currentTime);
}