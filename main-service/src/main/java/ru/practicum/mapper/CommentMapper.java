package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.model.Comment;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EventMapper.class, UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "created", source = "created", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "published", source = "published", qualifiedByName = "instantToLocalDateTime")
    CommentFullDto toFullDto(Comment comment);

    @Mapping(target = "created", source = "created", qualifiedByName = "instantToLocalDateTime")
    CommentShortDto toShortDto(Comment comment);

    List<CommentFullDto> toFullDto(Iterable<Comment> comments);

    List<CommentShortDto> toShortDto(Iterable<Comment> comments);
}
