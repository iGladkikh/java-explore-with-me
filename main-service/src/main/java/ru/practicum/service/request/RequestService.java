package ru.practicum.service.request;

import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateStatusDto;
import ru.practicum.dto.request.RequestUpdatedResultDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> findByUserIdAndEventId(Long userId, Long eventId);

    RequestUpdatedResultDto updateStatuses(Long userId, Long eventId, RequestUpdateStatusDto dto);

    List<RequestDto> findByUserId(Long userId);

    RequestDto create(Long userId, Long eventId);

    RequestDto cancel(Long userId, Long requestId);
}