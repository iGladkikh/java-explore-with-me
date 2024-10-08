package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant time) {
        return TimeUtil.instantToLocalDateTime(time);
    }

    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime time) {
        return TimeUtil.localDateTimeToInstant(time);
    }

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "localDateTimeToInstant")
    Event toModel(EventCreateDto dto);

    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "publishedOn", source = "published", qualifiedByName = "instantToLocalDateTime")
    EventFullDto toFullDto(Event event);

    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "instantToLocalDateTime")
    EventShortDto toShortDto(Event event);

    List<EventFullDto> toFullDto(Iterable<Event> events);

    List<EventShortDto> toShortDto(Iterable<Event> events);
}
