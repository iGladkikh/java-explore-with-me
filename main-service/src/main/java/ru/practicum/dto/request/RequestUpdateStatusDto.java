package ru.practicum.dto.request;

import lombok.Data;
import ru.practicum.model.enums.RequestState;

import java.util.List;

@Data
public class RequestUpdateStatusDto {
    private List<Long> requestIds;
    private RequestState status;
}