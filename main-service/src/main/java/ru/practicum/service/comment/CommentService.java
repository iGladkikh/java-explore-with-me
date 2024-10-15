package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.model.enums.CommentState;

import java.time.Instant;
import java.util.List;

public interface CommentService {

    List<CommentFullDto> findAllForAdmin(Iterable<Long> authors, Iterable<Long> events, Iterable<CommentState> states,
                                         Instant rangeStart, Instant rangeEnd, Integer from, Integer size);

    List<CommentShortDto> findAllByEventIdForPublic(Long eventId, Integer from, Integer size);

    CommentFullDto findByIdForAuthor(Long authorId, Long commentId);

    List<CommentFullDto> findByAuthorId(Long authorId, Integer from, Integer size);

    CommentFullDto create(Long authorId, CommentCreateDto dto);

    CommentFullDto updateMessage(Long authorId, Long commentId, CommentUpdateDto dto);

    CommentFullDto approve(Long id);

    CommentFullDto reject(Long id);

    void delete(Long id, Long authorId);
}
