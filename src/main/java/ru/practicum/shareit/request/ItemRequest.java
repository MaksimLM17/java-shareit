package ru.practicum.shareit.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemRequest {
    private Integer id;
    private String description;
    private Integer requestor;
    private Timestamp created;
}
