package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("SELECT i FROM Item i WHERE i.owner.id = :userId")
    List<Item> findItemsByUserId(Integer userId);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameAndDescription(String text);
}
