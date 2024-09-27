package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointRequestDto;
import ru.practicum.EndpointStatisticDto;
import ru.practicum.mapper.EndpointRequestMapper;
import ru.practicum.repository.StatsRepository;

import java.time.Instant;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public void saveEndpointRequest(EndpointRequestDto dto) {
        statsRepository.save(EndpointRequestMapper.toEntity(dto));
    }

    @Override
    public List<EndpointStatisticDto> getEndpointStatistic(Instant start, Instant end, Boolean useUniqueIp, String[] endpointUris) {
        if (start.plusMillis(1).isAfter(end)) {
            throw new RuntimeException(
                    String.format("Время начала периода выборки %s должна предшествовать времени окончания %s", start, end));
        }

        if (endpointUris.length == 0) {
            return useUniqueIp ?
                    statsRepository.getAllStatWithUniqueIp(start, end) :
                    statsRepository.getAllStat(start, end);
        } else {
            return useUniqueIp ?
                    statsRepository.getStatForUriWithUniqueIp(endpointUris, start, end) :
                    statsRepository.getStatForUri(endpointUris, start, end);
        }
    }
}
