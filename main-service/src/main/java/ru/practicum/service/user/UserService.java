package ru.practicum.service.user;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserFullDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {

    List<UserFullDto> findByIds(List<Long> ids, Pageable page);

    UserFullDto create(UserCreateDto dto);

    void delete(Long id);

    User getEntity(Long id);

    void checkForExistsById(Long id);

    void checkForExistsByEmail(String email);
}
