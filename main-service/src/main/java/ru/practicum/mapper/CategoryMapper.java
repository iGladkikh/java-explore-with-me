package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.category.CategoryCreateDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    Category toModel(CategoryCreateDto dto);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(Iterable<Category> categories);
}
