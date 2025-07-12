package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Integer requestorId);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Integer requestorId);

}

