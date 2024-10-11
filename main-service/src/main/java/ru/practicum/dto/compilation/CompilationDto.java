package ru.practicum.dto.compilation;

import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private Integer id;
    private String title;
    private List<EventShortDto> events;
    private Boolean pinned;
}