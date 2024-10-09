package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryCreateDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryCreateDto dto) {
        log.info("Create category (admin controller) with dto {}", dto);
        return categoryService.create(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryCreateDto dto) {
        log.info("Update category (admin controller) with params: id={}, dto {}", id, dto);
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.info("Delete category (admin controller) with id={}", id);
        categoryService.delete(id);
    }
}
