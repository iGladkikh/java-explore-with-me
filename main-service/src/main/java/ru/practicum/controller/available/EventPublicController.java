package ru.practicum.controller.available;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.common.PaginationUtil;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.enums.EventSort;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
public class EventPublicController {
    private final EventService eventService;

    @Autowired
    public EventPublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> findEventsByParams(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
                                                  LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
                                                  LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) Integer size,
                                                  HttpServletRequest request) {
        log.info("Find events with params: text={}, categories={}, paid={}, rangeStart={}, " +
                        "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return eventService.findManyForPublic(text, categories, paid,
                TimeUtil.localDateTimeToInstant(rangeStart),
                TimeUtil.localDateTimeToInstant(rangeEnd),
                onlyAvailable, sort,
                PaginationUtil.getPage(from, size), request);
    }

    @GetMapping("/{id}")
    public EventFullDto findEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("Find event with id={}", id);
        return eventService.findByIdForPublic(id, request);
    }
}
