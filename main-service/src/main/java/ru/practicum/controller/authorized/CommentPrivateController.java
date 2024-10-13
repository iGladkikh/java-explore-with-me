package ru.practicum.controller.authorized;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @Autowired
    public CommentPrivateController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentFullDto> findComments(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) @Positive Integer size) {
        log.info("Find comments (private controller) with userId={}", userId);
        return commentService.findByAuthorId(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto findComment(@PathVariable Long userId,
                                      @PathVariable Long commentId) {
        log.info("Find comment with id={} (private controller) for userId={}", commentId, userId);
        return commentService.findByIdForAuthor(userId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable Long userId, @RequestBody CommentCreateDto dto) {
        log.info("Create comment (private controller) with params: userId={},  dto{}", userId, dto);
        return commentService.create(userId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentFullDto updateCommentMessage(@PathVariable Long userId,
                                               @PathVariable Long commentId,
                                               @RequestBody CommentUpdateDto dto) {
        log.info("Update comment (private controller) with params: userId={}, commentId={}, dto{}", userId, commentId, dto);
        return commentService.updateMessage(userId, commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Delete comment (private controller) with params: userId={}, commentId={}", userId, commentId);
        commentService.delete(userId, commentId);
    }
}
