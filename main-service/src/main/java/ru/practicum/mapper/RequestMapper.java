package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Request;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant time) {
        return TimeUtil.instantToLocalDateTime(time);
    }

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "status", source = "state")
    @Mapping(target = "created", source = "created", qualifiedByName = "instantToLocalDateTime")
    RequestDto toDto(Request request);

    List<RequestDto> toDto(Iterable<Request> requests);
}
