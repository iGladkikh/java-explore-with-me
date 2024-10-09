package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointStatisticDto;
import ru.practicum.common.PaginationUtil;
import ru.practicum.common.StatisticUtil;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventUpdateByAdminDto;
import ru.practicum.dto.event.EventUpdateByUserDto;
import ru.practicum.dto.event.EventUpdateDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventSort;
import ru.practicum.model.enums.EventState;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatisticUtil statisticUtil;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;


    @Override
    public List<EventFullDto> findManyForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               Instant rangeStart, Instant rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Incorrect request: start of the event is after end of the event");
        }

        if (categories != null && categories.isEmpty()) {
            categories = null;
        }

        if (states != null && states.isEmpty()) {
            states = null;
        }
        Pageable page = PageRequest.of(from, size);
        List<Event> events = repository.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, page);
        statisticUtil.fillEventsViews(TimeUtil.instantToLocalDateTime(rangeStart), events);

        return eventMapper.toFullDto(events);
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, EventUpdateByAdminDto dto) {
        Event oldEvent = checkEventIsExistsAndGet(eventId);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!oldEvent.getState().equals(EventState.PENDING)) {
                        throw new ForbiddenException("Cannot publish the event because it's not in the right state: PENDING");
                    }
                    oldEvent.setState(EventState.PUBLISHED);
                    oldEvent.setPublished(Instant.now());
                    break;
                case REJECT_EVENT:
                    if (oldEvent.getState().equals(EventState.PUBLISHED)) {
                        throw new ForbiddenException("Cannot reject the event because it's not in the right state: PUBLISHED");
                    }
                    oldEvent.setState(EventState.CANCELED);
                    break;
            }
        }

        Event updatedEvent = repository.save(fillUpdatedFields(oldEvent, dto));
        statisticUtil.fillEventViews(updatedEvent);
        return eventMapper.toFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> findManyForUser(Long userId, Integer from, Integer size) {
        checkUserIsExistsAndGet(userId);
        Pageable page = PageRequest.of(from, size);
        List<Event> events = repository.findAllByInitiatorId(userId, page);
        statisticUtil.fillEventsViews(events);
        return eventMapper.toShortDto(events);
    }

    @Override
    public EventFullDto createByUser(Long userId, EventCreateDto dto) {
        User user = checkUserIsExistsAndGet(userId);
        Category category = checkCategoryIsExistsAndGet(dto.getCategory());
        Location location = locationRepository.save(dto.getLocation());

        Event event = eventMapper.toModel(dto);
        if (event.getPaid() == null) {
            event.setPaid(false);
        }

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(Instant.now());
        event.setState(EventState.PENDING);

        return eventMapper.toFullDto(repository.save(event));
    }

    @Override
    public EventFullDto findByIdForUser(Long userId, Long eventId) {
        checkUserIsExistsAndGet(userId);

        Event event = checkEventIsExistsAndGet(eventId);
        statisticUtil.fillEventViews(event);
        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, EventUpdateByUserDto dto) {
        checkUserIsExistsAndGet(userId);

        if (dto.getEventDate() != null && dto.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s".formatted(dto.getEventDate()));
        }

        Event oldEvent = checkEventIsExistsAndGet(eventId);
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Event must not be published");
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setState(EventState.CANCELED);
                    break;
            }
        }

        Event updatedEvent = repository.save(fillUpdatedFields(oldEvent, dto));
        statisticUtil.fillEventViews(updatedEvent);
        return eventMapper.toFullDto(updatedEvent);
    }

    @Override
    public EventFullDto findByIdForPublic(Long id, HttpServletRequest request) {
        Event event = repository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(()
                -> new DataNotFoundException("Event with id=%s was not found".formatted(id)));
        statisticUtil.addView(request.getRequestURI(), request.getRemoteAddr());
        statisticUtil.fillEventViews(event);
        return eventMapper.toFullDto(event);
    }

    @Override
    public List<EventShortDto> findManyForPublic(String text, List<Long> categories, Boolean paid,
                                                 Instant rangeStart, Instant rangeEnd,
                                                 Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
                                                 HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start of the event is after end of the event");
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = Instant.now();
        }

        if (categories != null && categories.isEmpty()) {
            categories = null;
        }

        statisticUtil.addView(request.getRequestURI(), request.getRemoteAddr());

        Pageable page = PaginationUtil.getPage(from, size);
        Set<Long> idsFilter = null;

        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    page = PaginationUtil.getPage(from, size, "eventDate", Sort.Direction.DESC);
                    break;
                case VIEWS:
                    Map<Long, EndpointStatisticDto> allStatistic = statisticUtil.getAllEventsStatistic(TimeUtil.instantToLocalDateTime(rangeStart));
                    idsFilter = allStatistic.keySet();
                    break;
            }
        }

        List<Event> events = repository.findPublished(idsFilter, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);
        Map<Long, EndpointStatisticDto> eventsStatistic = statisticUtil.getEventsStatistic(TimeUtil.instantToLocalDateTime(rangeStart), events);
        statisticUtil.fillEventsViews(events, eventsStatistic);

        if (sort == EventSort.VIEWS) {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getViews))
                    .toList();

        }

        return eventMapper.toShortDto(events);
    }

    private User checkUserIsExistsAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new DataNotFoundException("User with id=%s was not found".formatted(userId)));
    }

    private Event fillUpdatedFields(Event target, EventUpdateDto source) {

        if (source.getEventDate() != null) {
            if (source.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("EventDate must be one hour at least in the future");
            }
            target.setEventDate(TimeUtil.localDateTimeToInstant(source.getEventDate()));
        }

        if (source.getTitle() != null) {
            target.setTitle(source.getTitle());
        }

        if (source.getAnnotation() != null) {
            target.setAnnotation(source.getAnnotation());
        }

        if (source.getDescription() != null) {
            target.setDescription(source.getDescription());
        }

        if (source.getCategory() != null) {
            target.setCategory(checkCategoryIsExistsAndGet(source.getCategory()));
        }

        if (source.getLocation() != null) {
            target.setLocation(locationRepository.save(source.getLocation()));
        }

        if (source.getPaid() != null) {
            target.setPaid(source.getPaid());
        }

        if (source.getParticipantLimit() != null) {
            target.setParticipantLimit(source.getParticipantLimit());
        }

        if (source.getRequestModeration() != null) {
            target.setRequestModeration(source.getRequestModeration());
        }

        return target;
    }

    private Event checkEventIsExistsAndGet(Long eventId) {
        return repository.findById(eventId).orElseThrow(()
                -> new DataNotFoundException("Event with id=%s was not found".formatted(eventId)));
    }

    private Category checkCategoryIsExistsAndGet(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(()
                -> new DataNotFoundException("Category with id=%s was not found".formatted(categoryId)));
    }
}
