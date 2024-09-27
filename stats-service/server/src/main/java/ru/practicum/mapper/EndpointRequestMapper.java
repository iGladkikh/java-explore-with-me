package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.EndpointRequest;
import ru.practicum.EndpointRequestDto;

import java.time.Instant;
import java.time.ZoneId;

@UtilityClass
public class EndpointRequestMapper {
    static final ZoneId TIMEZONE_ID = ZoneId.systemDefault();

    public static EndpointRequest toEntity(EndpointRequestDto createDto) {
        if (createDto == null) {
            return null;
        }

        EndpointRequest request = new EndpointRequest();
        request.setApp(createDto.getApp());
        request.setUri(createDto.getUri());
        request.setIp(createDto.getIp());
        request.setTimestamp(Instant.from(createDto.getTimestamp().atZone(TIMEZONE_ID)));

        return request;
    }
}
