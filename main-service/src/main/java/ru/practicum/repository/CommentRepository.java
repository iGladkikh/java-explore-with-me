package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;
import ru.practicum.model.enums.CommentState;

import java.time.Instant;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "WHERE (:authors IS NULL OR c.author.id IN :authors) " +
            "AND (:events IS NULL OR c.event.id IN :events) " +
            "AND (:states IS NULL OR c.state IN :states) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR c.created > :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR c.created < :rangeEnd)")
    List<Comment> findAllForAdmin(Iterable<Long> authors, Iterable<Long> events, Iterable<CommentState> states,
                                  Instant rangeStart, Instant rangeEnd, Pageable page);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.event.id = :eventId " +
            "AND c.state = 'PUBLISHED'")
    List<Comment> findPublishedForEvent(Long eventId, Pageable page);

    List<Comment> findAllByAuthorId(Long authorId, Pageable page);

    boolean existsByAuthorIdAndEventIdAndStateIn(Long authorId, Long eventId, List<CommentState> states);
}
