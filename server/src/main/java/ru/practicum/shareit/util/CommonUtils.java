package ru.practicum.shareit.util;

import java.util.Comparator;
import java.util.Map;

public class CommonUtils {

    public static <T> Integer getNextId(Map<Integer, T> items) {
        int currentMaxId = items.keySet()
                .stream()
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(0);
        return ++currentMaxId;
    }
}
