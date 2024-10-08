package ru.practicum.service.compilation;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationCreateDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findPinned(Boolean pinned, Pageable page);

    CompilationDto findById(Long id);

    CompilationDto create(CompilationCreateDto dto);

    CompilationDto update(Long id, CompilationUpdateDto dto);

    void delete(Long id);
}
