package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointRequestDto;
import ru.practicum.EndpointStatisticDto;
import ru.practicum.service.StatsService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class StatsController {
    static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveEndpointRequest(@RequestBody @Valid EndpointRequestDto dto) {
        log.info("Save endpoint request with DTO: {}", dto);
        statsService.saveEndpointRequest(dto);
    }

    @GetMapping("/stats")
    public List<EndpointStatisticDto> getEndpointStatistic(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                           LocalDateTime start,
                                                           @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT)
                                                           LocalDateTime end,
                                                           @RequestParam(name = "unique", defaultValue = "false")
                                                           Boolean useUniqueIp,
                                                           @RequestParam(name = "uris", defaultValue = "")
                                                           String[] endpointUris) {
        log.info(String.format("Get statistic with params: start=%s, end=%s, unique=%s, uris=%s",
                start, end, useUniqueIp, Arrays.toString(endpointUris))
        );

        return statsService.getEndpointStatistic(
                Instant.from(start.atZone(ZoneId.systemDefault())),
                Instant.from(end.atZone(ZoneId.systemDefault())),
                useUniqueIp,
                endpointUris
        );
    }
}
