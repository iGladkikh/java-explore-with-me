package ru.practicum.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.model.enums.CommentState;
import ru.practicum.service.comment.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @Autowired
    public CommentAdminController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentFullDto> findComments(@RequestParam(required = false) List<Long> authors,
                                             @RequestParam(required = false) List<Long> events,
                                             @RequestParam(required = false) List<CommentState> states,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
                                             LocalDateTime rangeStart,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
                                             LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) Integer size) {
        log.info("Find comments (admin controller) with params: users={}, states={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                authors, states, rangeStart, rangeEnd, from, size);

        return commentService.findAllForAdmin(authors, events, states,
                TimeUtil.localDateTimeToInstant(rangeStart),
                TimeUtil.localDateTimeToInstant(rangeEnd),
                from, size);
    }

    @PatchMapping("/{id}/approve")
    public CommentFullDto approveComment(@PathVariable Long id) {
        log.info("Approve comment (admin controller) with id={}", id);
        return commentService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public CommentFullDto rejectComment(@PathVariable Long id) {
        log.info("Reject comment (admin controller) with id={}", id);
        return commentService.reject(id);
    }
}
