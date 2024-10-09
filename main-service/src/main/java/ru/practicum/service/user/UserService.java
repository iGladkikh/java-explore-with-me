package ru.practicum.service.user;

import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserFullDto;

import java.util.List;

public interface UserService {

    List<UserFullDto> findByIds(List<Long> ids, Integer from, Integer size);

    UserFullDto create(UserCreateDto dto);

    void delete(Long id);

    void checkForExistsById(Long id);

    void checkForExistsByEmail(String email);
}
