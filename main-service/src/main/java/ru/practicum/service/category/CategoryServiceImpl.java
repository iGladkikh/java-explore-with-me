package ru.practicum.service.category;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.PaginationUtil;
import ru.practicum.dto.category.CategoryCreateDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Pageable page = PaginationUtil.getPage(from, size);
        return mapper.toDto(repository.findAll(page));
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category with id=%s was not found".formatted(id)));
        return mapper.toDto(category);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CategoryDto create(CategoryCreateDto dto) {
        try {
            return mapper.toDto(repository.save(mapper.toModel(dto)));
        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("This category name is exists already in database");
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Category with id=%s was not found".formatted(id));
        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("The category is not empty");
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CategoryDto update(Long id, CategoryCreateDto dto) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category with id=%s was not found".formatted(id)));

        if (category.getName().equals(dto.getName())) {
            return mapper.toDto(category);
        }
        category.setName(dto.getName());

        try {
            return mapper.toDto(repository.save(category));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new DataConflictException("This category name is already exists");
        }
    }
}
