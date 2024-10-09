package ru.practicum.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.common.TimeUtil;
import ru.practicum.model.enums.RequestState;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private RequestState status;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime created;
}