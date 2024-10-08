package ru.practicum.controller.authorized;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/")
public class RequestPrivateController {
    private final RequestService requestService;

    @Autowired
    public RequestPrivateController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> findRequests(@PathVariable Long userId) {
        log.info("Find requests (private controller) with userId={}", userId);
        return requestService.findByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Create request (private controller) with params: userId={}, eventId={}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Cancel request (private controller) with params: userId={}, requestId={}", userId, requestId);
        return requestService.cancel(userId, requestId);
    }
}
