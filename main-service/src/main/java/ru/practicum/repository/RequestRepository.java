package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;
import ru.practicum.model.enums.RequestState;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    Request findByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByState(RequestState state);
}