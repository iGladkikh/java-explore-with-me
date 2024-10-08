package ru.practicum.dto.user;

import lombok.Data;

@Data
public class UserFullDto {
    private Long id;
    private String name;
    private String email;
}