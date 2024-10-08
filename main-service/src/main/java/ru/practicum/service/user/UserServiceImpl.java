package ru.practicum.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserFullDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserFullDto create(UserCreateDto dto) {
        checkForExistsByEmail(dto.getEmail());
        return mapper.toFullDto(repository.save(mapper.toModel(dto)));
    }

    @Override
    public List<UserFullDto> findByIds(List<Long> ids, Pageable page) {
        if (ids == null || ids.isEmpty()) {
            return mapper.toFullDto(repository.findAll(page));
        }
        return mapper.toFullDto(repository.getUsersByIdIn(ids, page));
    }

    @Override
    public void delete(Long id) {
        checkForExistsById(id);
        repository.deleteById(id);
    }

    @Override
    public User getEntity(Long id) {
        return repository.findById(id).orElseThrow(()
                -> new DataNotFoundException("User with id=%s was not found".formatted(id)));
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