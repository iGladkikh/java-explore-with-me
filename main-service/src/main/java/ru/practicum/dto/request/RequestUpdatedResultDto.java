package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestUpdatedResultDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}