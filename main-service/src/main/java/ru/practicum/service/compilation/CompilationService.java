package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationCreateDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> findPinned(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long id);

    CompilationDto create(CompilationCreateDto dto);

    CompilationDto update(Long id, CompilationUpdateDto dto);

    void delete(Long id);
}
