package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class StatClient {
    private final RestTemplate restTemplate;

    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<Object> saveEndpointRequest(EndpointRequestDto dto) {
        return sendRequest(HttpMethod.POST, "/hit", dto);
    }

    public ResponseEntity<Object> getEndpointStatistic(LocalDateTime start, LocalDateTime end, String endpointUris, Boolean useUniqueIp) {
        Map<String, Object> parameters = Map.of(
                "start", URLEncoder.encode(start.toString(), StandardCharsets.UTF_8),
                "end", URLEncoder.encode(end.toString(), StandardCharsets.UTF_8),
                "uris", endpointUris,
                "unique", useUniqueIp
        );
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        return sendRequest(HttpMethod.GET, path, parameters);
    }

    private <T> ResponseEntity<Object> sendRequest(HttpMethod method, String path, T body) {
        return sendRequest(method, path, null, body);
    }

    private ResponseEntity<Object> sendRequest(HttpMethod method, String path, Map<String, Object> parameters) {
        return sendRequest(method, path, parameters, null);
    }

    private <T> ResponseEntity<Object> sendRequest(HttpMethod method,
                                                   String path,
                                                   @Nullable Map<String, Object> parameters,
                                                   @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, prepareHeaders());

        ResponseEntity<Object> response;
        try {
            if (parameters != null) {
                response = restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                response = restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareResponse(response);
    }

    private HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}