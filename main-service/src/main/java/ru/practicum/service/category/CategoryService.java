package ru.practicum.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryCreateDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto create(CategoryCreateDto dto);

    CategoryDto update(Long id, CategoryCreateDto dto);

    void delete(Long id);
}