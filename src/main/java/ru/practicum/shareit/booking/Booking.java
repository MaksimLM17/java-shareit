package ru.practicum.shareit.booking;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Booking {
    private Integer id;
    private Timestamp start;
    private Timestamp end;
    private Integer itemId;
    private Integer userId;
    private Status status;
}
