package ru.practicum.service;

import ru.practicum.EndpointRequestDto;
import ru.practicum.EndpointStatisticDto;

import java.time.Instant;
import java.util.List;

public interface StatsService {

    void saveEndpointRequest(EndpointRequestDto dto);

    List<EndpointStatisticDto> getEndpointStatistic(Instant start, Instant end, Boolean useUniqueIp, String[] endpointUris);
}
