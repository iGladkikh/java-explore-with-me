package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserFullDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toModel(UserCreateDto dto);

    UserFullDto toFullDto(User user);

    UserShortDto toShortDto(User user);

    List<UserFullDto> toFullDto(Iterable<User> users);
}
