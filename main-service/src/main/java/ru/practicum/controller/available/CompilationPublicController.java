package ru.practicum.controller.available;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.AppConstants;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam(required = false) Boolean pinned,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = AppConstants.DEFAULT_CONTROLLER_LIST_SIZE) @Positive Integer size) {
        log.info("Find compilations (public controller) with params: pinned {}, from {}, size {}", pinned, from, size);
        return service.findPinned(pinned, from, size);
    }

    @GetMapping("/{id}")
    public CompilationDto findCompilation(@PathVariable Long id) {
        log.info("Find compilation (public controller) with id={}", id);
        return service.findById(id);
    }
}
