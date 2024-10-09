package ru.practicum.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.EndpointRequestDto;
import ru.practicum.EndpointStatisticDto;
import ru.practicum.StatClient;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StatisticUtil {
    private static final String EVENT_ENDPOINT_PATH = "/events/";
    private final StatClient statClient;

    @Autowired
    public StatisticUtil(StatClient statClient) {
        this.statClient = statClient;
    }

    public void fillEventViews(Event event) {
        fillEventsViews(List.of(event));
    }

    public void fillEventsViews(List<Event> events) {
        fillEventsViews(null, events);
    }

    public void fillEventsViews(LocalDateTime start, List<Event> events) {
        Map<Long, EndpointStatisticDto> eventsStatistic = getEventsStatistic(start, events);
        fillEventsViews(events, eventsStatistic);
    }

    public void fillEventsViews(List<Event> events, Map<Long, EndpointStatisticDto> eventsStatistic) {
        for (Event event : events) {
            Long eventId = event.getId();
            if (eventsStatistic.containsKey(eventId)) {
                event.setViews(eventsStatistic.get(event.getId()).getHits());
            } else {
                event.setViews(0L);
            }
        }
    }

    public Map<Long, EndpointStatisticDto> getAllEventsStatistic(LocalDateTime start) {
        return getEventsStatistic(start, null);
    }

    public Map<Long, EndpointStatisticDto> getEventsStatistic(LocalDateTime start, List<Event> events) {
        if (start == null) {
            start = LocalDateTime.of(-1, 1, 1, 0, 0);
        }

        if (events == null) {
            events = Collections.emptyList();
        }

        List<String> endpointUris = new ArrayList<>();
        for (Event event : events) {
            endpointUris.add(getEventEndpointUri(event.getId()));
        }
        return Objects.requireNonNull(statClient.getEndpointsStatistic(start, LocalDateTime.now().plusSeconds(1L),
                                endpointUris.toArray(new String[0]), true)
                        .getBody()).stream()
                .collect(Collectors.toMap(s -> getEventIdFromEndpointUri(s.getUri()), Function.identity()));
    }

    public void addView(String uri, String ip) {
        EndpointRequestDto dto = new EndpointRequestDto();
        dto.setUri(uri);
        dto.setIp(ip);
        dto.setApp(AppConstants.APP_NAME);
        dto.setTimestamp(LocalDateTime.now());

        statClient.saveEndpointRequest(dto);
    }


    private String getEventEndpointUri(long id) {
        return EVENT_ENDPOINT_PATH + id;
    }

    private Long getEventIdFromEndpointUri(String uri) {
        return Long.parseLong(uri.replace(EVENT_ENDPOINT_PATH, ""));
    }
}
