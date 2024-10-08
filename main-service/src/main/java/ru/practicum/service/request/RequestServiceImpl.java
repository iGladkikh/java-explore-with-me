package ru.practicum.service.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateStatusDto;
import ru.practicum.dto.request.RequestUpdatedResultDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.RequestState;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, EventRepository eventRepository, RequestMapper mapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RequestDto> findByUserIdAndEventId(Long userId, Long eventId) {
        return List.of();
    }

    @Override
    public RequestUpdatedResultDto updateStatuses(Long userId, Long eventId, RequestUpdateStatusDto dto) {
        return null;
    }

    @Override
    public List<RequestDto> findByUserId(Long userId) {
        checkUserIsExistsAndGet(userId);
        return mapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public RequestDto create(Long userId, Long eventId) {
        User requester = checkUserIsExistsAndGet(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new DataNotFoundException("Event with id=%s was not found".formatted(eventId)));

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId) != null) {
            throw new DataConflictException("There is already a request from user to the event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Event not in the right state: PUBLISHED");
        }

        if (event.getInitiator().equals(requester)) {
            throw new DataConflictException("The requester must not be event's initiator");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new DataConflictException("The event has already reached its participant limit=%s"
                    .formatted(event.getParticipantLimit()));
        }

        Request request = new Request();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(Instant.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setState(RequestState.CONFIRMED);
        } else {
            request.setState(RequestState.PENDING);
        }

        return mapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancel(Long userId, Long requestId) {
        checkUserIsExistsAndGet(userId);

        Request request = requestRepository.findById(requestId).orElseThrow(()
                -> new DataNotFoundException("Request with id=%s was not found".formatted(requestId)));
        request.setState(RequestState.CANCELED);

        return mapper.toDto(requestRepository.save(request));
    }

    private User checkUserIsExistsAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new DataNotFoundException("User with id=%s was not found".formatted(userId)));
    }
}