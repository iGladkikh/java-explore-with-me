package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.PaginationUtil;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.enums.CommentState;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.RequestState;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper mapper;

    @Override
    public List<CommentFullDto> findAllForAdmin(Iterable<Long> authors, Iterable<Long> events, Iterable<CommentState> states,
                                                Instant rangeStart, Instant rangeEnd, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findAllForAdmin(authors, events, states,
                rangeStart, rangeEnd, PaginationUtil.getPage(from, size));
        return mapper.toFullDto(comments);
    }

    @Override
    public List<CommentShortDto> findAllByEventIdForPublic(Long eventId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findPublishedForEvent(eventId, PaginationUtil.getPage(from, size));
        return mapper.toShortDto(comments);
    }

    @Override
    public CommentFullDto findByIdForAuthor(Long authorId, Long commentId) {
        checkAuthorForExists(authorId);
        Comment comment = checkCommentForExistsAndGet(commentId);
        checkCommentForAuthorship(comment, authorId);

        return mapper.toFullDto(comment);
    }

    @Override
    public List<CommentFullDto> findByAuthorId(Long authorId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findAllByAuthorId(authorId, PaginationUtil.getPage(from, size));
        return mapper.toFullDto(comments);
    }

    @Override
    @Transactional
    public CommentFullDto create(Long authorId, CommentCreateDto dto) {
        User author = checkAuthorForExistsAndGet(authorId);
        Event event = checkEventForExistsAndGet(dto.getEvent());
        checkPublishedCommentForExists(authorId, dto.getEvent());

        if (event.getInitiator().getId().equals(authorId)) {
            throw new ForbiddenException("You can't create a comment for your own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new DataConflictException("Event not in the right state: PUBLISHED");
        }

        if (event.getEventDate().isBefore(Instant.now())) {
            throw new DataConflictException("Event date has not yet arrived");
        }

        Request request = requestRepository.findByEventIdAndRequesterId(event.getId(), authorId);
        if (request == null || request.getState() != RequestState.CONFIRMED) {
            throw new ForbiddenException("The author of the comment did not participate in the event");
        }

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setMessage(dto.getMessage());
        comment.setCreated(Instant.now());
        comment.setState(CommentState.NEW);

        return mapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentFullDto updateMessage(Long authorId, Long commentId, CommentUpdateDto dto) {
        Comment comment = checkCommentForExistsAndGet(commentId);
        checkCommentForAuthorship(comment, authorId);
        comment.setMessage(dto.getMessage());
        comment.setState(CommentState.NEW);
        comment.setPublished(null);

        return mapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentFullDto approve(Long id) {
        return updateState(id, CommentState.PUBLISHED);
    }

    @Override
    @Transactional
    public CommentFullDto reject(Long id) {
        return updateState(id, CommentState.REJECTED);
    }

    private CommentFullDto updateState(Long id, CommentState state) {
        if (state == CommentState.NEW) {
            throw new BadRequestException("New comment state must be: PUBLISHED or REJECTED");
        }

        Comment comment = checkCommentForExistsAndGet(id);
        if (comment.getState() != CommentState.NEW) {
            throw new DataConflictException("Comment not in the right state: NEW");
        }

        if (state == CommentState.PUBLISHED) {
            comment.setPublished(Instant.now());
        }

        comment.setState(state);
        return mapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long authorId, Long commentId) {
        Comment comment = checkCommentForExistsAndGet(commentId);
        checkCommentForAuthorship(comment, authorId);
        commentRepository.deleteById(commentId);
    }

    private static void checkCommentForAuthorship(Comment comment, Long authorId) {
        if (!Objects.equals(comment.getAuthor().getId(), authorId)) {
            throw new ForbiddenException("User with id=%s in not author for comment with id=%s"
                    .formatted(authorId, comment.getId()));
        }
    }

    private void checkPublishedCommentForExists(Long authorId, Long eventId) {
        if (commentRepository.existsByAuthorIdAndEventIdAndStateIn(authorId, eventId,
                List.of(CommentState.NEW, CommentState.PUBLISHED))) {
            throw new ForbiddenException("Comment from author with id=%s for event with id=%s already exists"
                    .formatted(authorId, eventId));
        }
    }

    private Comment checkCommentForExistsAndGet(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(()
                -> new DataNotFoundException("Comment with id=%s was not found".formatted(commentId)));
    }

    private void checkEventForExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataNotFoundException("Event with id=%s was not found".formatted(eventId));
        }
    }

    private Event checkEventForExistsAndGet(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(()
                -> new DataNotFoundException("Event with id=%s was not found".formatted(eventId)));
    }

    private void checkAuthorForExists(Long authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new DataNotFoundException("Author with id=%s was not found".formatted(authorId));
        }
    }

    private User checkAuthorForExistsAndGet(Long authorId) {
        return userRepository.findById(authorId).orElseThrow(()
                -> new DataNotFoundException("Author with id=%s was not found".formatted(authorId)));
    }
}
