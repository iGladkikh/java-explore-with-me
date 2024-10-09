package ru.practicum.controller.available;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> findCategories(@RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) int size) {
        log.info("Find categories (public controller) with params: from {}, size {}", from, size);
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto findCategory(@PathVariable Long id) {
        log.info("Find category (public controller) with id={}", id);
        return categoryService.findById(id);
    }
}
