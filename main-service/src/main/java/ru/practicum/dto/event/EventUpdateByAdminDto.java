package ru.practicum.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.model.enums.EventAdminAction;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventUpdateByAdminDto extends EventUpdateDto {
    private EventAdminAction stateAction;
}