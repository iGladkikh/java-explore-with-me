package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatClient {
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final RestTemplate restTemplate;

    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) throws RestClientException {
        restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public void saveEndpointRequest(EndpointRequestDto dto) {
        restTemplate.exchange("/hit", HttpMethod.POST, new HttpEntity<>(dto, prepareHeaders()), Object.class);
    }

    public ResponseEntity<List<EndpointStatisticDto>> getEndpointsStatistic(LocalDateTime start, LocalDateTime end, String[] endpointUris, Boolean useUniqueIp) {
        Map<String, Object> parameters = Map.of(
                "start", localDateTimeToString(start),
                "end", localDateTimeToString(end),
                "uris", endpointUris,
                "unique", useUniqueIp
        );

        return restTemplate.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                new HttpEntity<>(prepareHeaders()),
                new ParameterizedTypeReference<>() {
                }, parameters);
    }

    private HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
    }
}