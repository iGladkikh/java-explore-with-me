package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Location;
import ru.practicum.model.enums.EventState;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private Long views;
    private Integer confirmedRequests;
}