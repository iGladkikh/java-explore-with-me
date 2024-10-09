package ru.practicum.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.model.enums.EventUserAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventUpdateByUserDto extends EventUpdateDto {
    private EventUserAction stateAction;
}