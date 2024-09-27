package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EndpointRequestDto {
    @NotBlank(message = "Не указано название приложения")
    private String app;
    @NotBlank(message = "Не указан URL")
    private String uri;
    @NotBlank(message = "Не указан ip-адрес")
    private String ip;
    @NotNull(message = "Не указано время")
    @PastOrPresent(message = "Введенное время некорректно")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
