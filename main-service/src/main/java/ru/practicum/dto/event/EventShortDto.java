package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private Long views;
}