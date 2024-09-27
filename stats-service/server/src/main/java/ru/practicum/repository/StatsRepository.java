package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.EndpointStatisticDto;
import ru.practicum.model.EndpointRequest;

import java.time.Instant;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointRequest, Long> {

    @Query(value = "SELECT new ru.practicum.EndpointStatisticDto(er.app, er.uri, COUNT(er.ip)) " +
            "FROM EndpointRequest AS er " +
            "WHERE er.timestamp BETWEEN :start AND :end " +
            "GROUP BY er.uri, er.app " +
            "ORDER BY COUNT(er.ip) DESC")
    List<EndpointStatisticDto> getAllStat(Instant start, Instant end);

    @Query(value = "SELECT new ru.practicum.EndpointStatisticDto(er.app, er.uri, COUNT(DISTINCT er.ip)) " +
            "FROM EndpointRequest AS er " +
            "WHERE er.timestamp BETWEEN :start AND :end " +
            "GROUP BY er.uri, er.app " +
            "ORDER BY COUNT(DISTINCT er.ip) DESC")
    List<EndpointStatisticDto> getAllStatWithUniqueIp(Instant start, Instant end);

    @Query(value = "SELECT new ru.practicum.EndpointStatisticDto(er.app, er.uri, COUNT(er.ip)) " +
            "FROM EndpointRequest AS er " +
            "WHERE er.uri IN :endpointUris AND er.timestamp BETWEEN :start AND :end " +
            "GROUP BY er.uri, er.app " +
            "ORDER BY COUNT(er.ip) DESC")
    List<EndpointStatisticDto> getStatForUri(String[] endpointUris, Instant start, Instant end);

    @Query(value = "SELECT new ru.practicum.EndpointStatisticDto(er.app, er.uri, COUNT(DISTINCT er.ip)) " +
            "FROM EndpointRequest AS er " +
            "WHERE er.uri in :endpointUris AND er.timestamp BETWEEN :start and :end " +
            "GROUP BY er.uri, er.app " +
            "ORDER BY COUNT(DISTINCT er.ip) DESC")
    List<EndpointStatisticDto> getStatForUriWithUniqueIp(String[] endpointUris, Instant start, Instant end);
}
