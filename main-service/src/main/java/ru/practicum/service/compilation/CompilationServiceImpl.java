package ru.practicum.service.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.common.StatisticUtil;
import ru.practicum.dto.compilation.CompilationCreateDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationUpdateDto;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;
    private final StatisticUtil statisticUtil;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository, CompilationMapper mapper, StatisticUtil statisticUtil) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
        this.statisticUtil = statisticUtil;
    }

    @Override
    public List<CompilationDto> findPinned(Boolean pinned, Pageable page) {
        return mapper.toDto(compilationRepository.findAllByPinned(pinned, page));
    }

    @Override
    public CompilationDto findById(Long id) {
        return mapper.toDto(compilationRepository.findById(id).orElseThrow(()
                -> new DataNotFoundException("Compilation with id=%s was not found".formatted(id))));
    }

    @Override
    public CompilationDto create(CompilationCreateDto dto) {
        Compilation compilation = mapper.toModel(dto);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            compilation.setEvents(events);
            events.forEach(statisticUtil::fillEventViews);
        }

        return mapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(Long id, CompilationUpdateDto dto) {

        Compilation oldCompilation = compilationRepository.findById(id).orElseThrow(()
                -> new DataNotFoundException("Compilation with id=%s was not found".formatted(id)));

        if (dto.getTitle() != null) {
            oldCompilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            oldCompilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(dto.getEvents());
            oldCompilation.setEvents(events);
            events.forEach(statisticUtil::fillEventViews);
        }

        return mapper.toDto(compilationRepository.save(oldCompilation));
    }

    @Override
    public void delete(Long id) {
        findById(id);
        compilationRepository.deleteById(id);
    }
}