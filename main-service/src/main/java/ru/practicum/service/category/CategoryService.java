package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryCreateDto;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long id);

    CategoryDto create(CategoryCreateDto dto);

    CategoryDto update(Long id, CategoryCreateDto dto);

    void delete(Long id);
}