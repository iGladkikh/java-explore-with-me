package ru.practicum.service.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateStatusDto;
import ru.practicum.dto.request.RequestUpdatedResultDto;
import ru.practicum.exception.BadRequestException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
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
        checkUserIsExistsAndGet(userId);
        checkEventIsExistsAndGet(eventId);

        return mapper.toDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestUpdatedResultDto updateStatuses(Long userId, Long eventId, RequestUpdateStatusDto dto) {
        checkUserIsExistsAndGet(userId);

        Event event = checkEventIsExistsAndGet(eventId);
        List<Request> requests = requestRepository.findAllByIdIn(dto.getRequestIds());

        List<RequestDto> confirmedRequests = new ArrayList<>();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            for (Request request : requests) {
                request.setState(RequestState.CONFIRMED);
                confirmedRequests.add(mapper.toDto(requestRepository.save(request)));
            }
            return new RequestUpdatedResultDto(confirmedRequests, new ArrayList<>());
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new DataConflictException("The event has reached participant limit=%s"
                    .formatted(event.getParticipantLimit()));
        }

        List<RequestDto> rejectedRequests = new ArrayList<>();
        for (Request request : requests) {
            if (!request.getState().equals(RequestState.PENDING)) {
                throw new BadRequestException("The request is not in status: PENDING");
            }

            if (dto.getStatus() == RequestState.CONFIRMED) {
                if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new DataConflictException("The event has reached its participant limit=%s".formatted(event.getParticipantLimit()));
                }
                request.setState(RequestState.CONFIRMED);
                confirmedRequests.add(mapper.toDto(requestRepository.save(request)));
            } else {
                request.setState(RequestState.REJECTED);
                rejectedRequests.add(mapper.toDto(requestRepository.save(request)));
            }
        }

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            for (Request request : requestRepository.findAllByState(RequestState.PENDING)) {
                request.setState(RequestState.REJECTED);
                rejectedRequests.add(mapper.toDto(requestRepository.save(request)));
            }
        }
        return new RequestUpdatedResultDto(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<RequestDto> findByUserId(Long userId) {
        checkUserIsExistsAndGet(userId);
        return mapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
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
    @Transactional
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

    private Event checkEventIsExistsAndGet(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(()
                -> new DataNotFoundException("Event with id=%s was not found".formatted(eventId)));
    }
}