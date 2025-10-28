package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO для частичного обновления данных предмета.
 * <p>
 * Используется для операций PATCH-обновления, где изменяются только указанные поля.
 * Все поля являются опциональными - обновляются только те поля, которые не равны null.
 * </p>
 *
 * <p><b>Особенности обновления:</b></p>
 * <ul>
 *   <li>Поля со значением null игнорируются при обновлении</li>
 *   <li>Поддерживается частичное обновление любого набора полей</li>
 * </ul>
 * @author MaksimLM17
 * @version 1.0
 * @see ItemDto
 * @see ru.practicum.shareit.item.ItemService
 * @since 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {

    private String name;
    private String description;
    private Boolean available;
}
