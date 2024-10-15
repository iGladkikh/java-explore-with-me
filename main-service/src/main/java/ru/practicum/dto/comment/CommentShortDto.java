package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.common.TimeUtil;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class CommentShortDto {
    private Long id;
    private String message;
    private UserShortDto author;
    @JsonFormat(pattern = TimeUtil.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime created;
}
