package ru.practicum.common;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public final class PaginationUtil {
    private static final String DEFAULT_SORTED_FIELD = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    public Pageable getPage(int from, int size) {
        return PageRequest.of(calcPageNumber(from, size), size, Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORTED_FIELD));
    }

    public Pageable getPage(int from, int size, String field) {
        return PageRequest.of(calcPageNumber(from, size), size, Sort.by(DEFAULT_SORT_DIRECTION, field));
    }

    public Pageable getPage(int from, int size, String field, Sort.Direction sortDirection) {
        return PageRequest.of(calcPageNumber(from, size), size, Sort.by(sortDirection, field));
    }

    private int calcPageNumber(int from, int size) {
        return from / size;
    }
}
