package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventUpdateByAdminDto;
import ru.practicum.dto.event.EventUpdateByUserDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateStatusDto;
import ru.practicum.dto.request.RequestUpdatedResultDto;
import ru.practicum.model.enums.EventSort;
import ru.practicum.model.enums.EventState;

import java.time.Instant;
import java.util.List;

public interface EventService {

    /*** Admin controller ***/
    List<EventFullDto> findManyForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                        Instant rangeStart, Instant rangeEnd, Pageable page);

    EventFullDto updateByAdmin(Long eventId, EventUpdateByAdminDto dto);

    /*** Private controller ***/
    List<EventShortDto> findManyForUser(Long userId, Pageable page);

    EventFullDto findByIdForUser(Long userId, Long eventId);

    EventFullDto createByUser(Long userId, EventCreateDto eventDto);

    EventFullDto updateByUser(Long userId, Long eventId, EventUpdateByUserDto dto);

    List<RequestDto> getEventRequests(Long userId, Long eventId);

    RequestUpdatedResultDto updateRequestStatus(Long userId, Long eventId, RequestUpdateStatusDto request);

    /*** Public controller ***/
    List<EventShortDto> findManyForPublic(String text, List<Long> categories, Boolean paid,
                                          Instant rangeStart, Instant rangeEnd, Boolean onlyAvailable,
                                          EventSort sort, Pageable page, HttpServletRequest request);

    EventFullDto findByIdForPublic(Long id, HttpServletRequest request);
}