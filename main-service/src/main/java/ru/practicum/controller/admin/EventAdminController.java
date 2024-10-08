package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.common.PaginationUtil;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventUpdateByAdminDto;
import ru.practicum.model.enums.EventState;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @Autowired
    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) Integer size,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                         @RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<EventState> states,
                                         @RequestParam(required = false) List<Long> categories) {
        log.info("Find events (admin controller) with params: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.findManyForAdmin(users, states, categories,
                TimeUtil.localDateTimeToInstant(rangeStart),
                TimeUtil.localDateTimeToInstant(rangeEnd),
                PaginationUtil.getPage(from, size));
    }

    @PatchMapping("/{id}")
    public EventFullDto updateEvent(@PathVariable Long id, @RequestBody @Valid EventUpdateByAdminDto dto) {
        log.info("Update event (admin controller) with id={} using dto {}", id, dto);
        return eventService.updateByAdmin(id, dto);
    }
}
