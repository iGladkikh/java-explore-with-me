package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserFullDto;
import ru.practicum.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserService userService;

    @Autowired
    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserFullDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) int size) {
        log.info("Find users (admin controller) with params: ids={}, from={}, size={}", ids, from, size);
        return userService.findByIds(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullDto createUser(@RequestBody @Valid UserCreateDto dto) {
        log.info("Create user (admin controller) with dto {}", dto);
        return userService.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Delete user (admin controller) with id={}", id);
        userService.delete(id);
    }
}