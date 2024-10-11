package ru.practicum.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.PaginationUtil;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserFullDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserFullDto create(UserCreateDto dto) {
        checkForExistsByEmail(dto.getEmail());
        return mapper.toFullDto(repository.save(mapper.toModel(dto)));
    }

    @Override
    public List<UserFullDto> findByIds(List<Long> ids, Integer from, Integer size) {
        Pageable page = PaginationUtil.getPage(from, size);
        if (ids == null || ids.isEmpty()) {
            return mapper.toFullDto(repository.findAll(page));
        }
        return mapper.toFullDto(repository.getUsersByIdIn(ids, page));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkForExistsById(id);
        repository.deleteById(id);
    }

    @Override
    public void checkForExistsById(Long id) {
        if (!repository.existsById(id)) {
            throw new DataNotFoundException("User with id=%s was not found".formatted(id));
        }
    }

    @Override
    public void checkForExistsByEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new DataConflictException("User with email=%s already exists".formatted(email));
        }
    }
}
