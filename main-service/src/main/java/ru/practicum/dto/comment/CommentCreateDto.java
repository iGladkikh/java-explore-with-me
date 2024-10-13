package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDto {
    @NotBlank
    @Size(min = 3, max = 2000)
    private String message;
    @NotNull
    private Long event;
}
