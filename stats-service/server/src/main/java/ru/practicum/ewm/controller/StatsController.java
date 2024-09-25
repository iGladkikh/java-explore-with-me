package ru.practicum.ewm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void set() {

    }
}
