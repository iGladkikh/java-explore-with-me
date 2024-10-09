package ru.practicum.controller.authorized;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventUpdateByUserDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateStatusDto;
import ru.practicum.dto.request.RequestUpdatedResultDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.request.RequestService;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @Autowired
    public EventPrivateController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @GetMapping
    public List<EventShortDto> findEvents(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) @Positive Integer size) {
        log.info("Find events (private controller) with params: userId={}, from {}, size {}", userId, from, size);
        return eventService.findManyForUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Find event (private controller) with params: userId={}, eventId {}", userId, eventId);
        return eventService.findByIdForUser(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid EventCreateDto dto) {
        log.info("Create event (private controller) with params: userId={}, dto {}", userId, dto);
        return eventService.createByUser(userId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody @Valid EventUpdateByUserDto dto) {
        log.info("Update event (private controller) with params: userId={}, eventId={}, dto {}", userId, eventId, dto);
        return eventService.updateByUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> findEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Find event requests (private controller) with params: userId={}, eventId {}", userId, eventId);
        return requestService.findByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdatedResultDto changeRequestStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                       @RequestBody RequestUpdateStatusDto dto) {
        log.info("Change event request status (private controller) with params: userId={}, eventId={}, dto {}", userId, eventId, dto);
        return requestService.updateStatuses(userId, eventId, dto);
    }
}
