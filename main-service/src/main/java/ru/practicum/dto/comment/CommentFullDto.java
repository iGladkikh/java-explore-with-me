package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.event.EventBaseDto;
import ru.practicum.dto.user.UserFullDto;
import ru.practicum.model.enums.CommentState;

import java.time.LocalDateTime;

@Data
public class CommentFullDto {
    private Long id;
    private String message;
    private EventBaseDto event;
    private UserFullDto author;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime created;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime published;
    private CommentState state;
}
