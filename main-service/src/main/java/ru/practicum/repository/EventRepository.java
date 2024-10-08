package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.enums.EventState;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (COALESCE(:rangeStart, null) IS NULL OR e.eventDate > :rangeStart) " +
            "AND (COALESCE(:rangeEnd, null) IS NULL OR e.eventDate < :rangeEnd)")
    List<Event> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                               Instant rangeStart, Instant rangeEnd, Pageable page);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable page);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:ids IS NULL OR e.id IN :ids) " +
            "AND (TRIM(:text) IS NULL OR " +
            "(UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')) OR UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%'))))" +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (COALESCE(:rangeStart, null) IS NULL OR e.eventDate > :rangeStart) " +
            "AND (COALESCE(:rangeEnd, null) IS NULL OR e.eventDate < :rangeEnd) " +
            "AND (:onlyAvailable IS NULL OR e.confirmedRequests < e.participantLimit OR e.participantLimit = 0)")
    List<Event> findPublished(Set<Long> ids, String text, List<Long> categories, Boolean paid, Instant rangeStart,
                              Instant rangeEnd, Boolean onlyAvailable, Pageable page);
}
