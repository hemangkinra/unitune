package in.weekend.unitune.configuration.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.weekend.unitune.dto.external.applemusic.AppleMusicFetchResponse;
import in.weekend.unitune.dto.external.applemusic.AppleMusicSearchResponse;
import in.weekend.unitune.dto.webclient.AppleMusicRestConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Slf4j
@Configuration
public class AppleMusicWebClientConfig {

    private final WebClient appleMusicWebClient;

    private final AppleMusicRestConfigDto appleMusicRestConfigDto;

    private final ObjectMapper objectMapper;


    public AppleMusicWebClientConfig(WebClient.Builder wcBuilder, AppleMusicRestConfigDto appleMusicRestConfigDto) {
        this.appleMusicRestConfigDto = appleMusicRestConfigDto;
        this.objectMapper = new ObjectMapper();

        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3));

        this.appleMusicWebClient = wcBuilder
                .baseUrl(appleMusicRestConfigDto.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }


    public Mono<AppleMusicSearchResponse> searchSong(String searchString) {
        return appleMusicWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(appleMusicRestConfigDto.getSearchMusicEndpoint())
                        .queryParam("term", searchString)
                        .queryParam("entity", "song")
                        .queryParam("limit", 1)
                        .build())
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(jsonString -> {
                                    try {
                                        AppleMusicSearchResponse response = objectMapper.readValue(jsonString, AppleMusicSearchResponse.class);
                                        return Mono.just(response);
                                    } catch (Exception e) {
                                        log.error("[APPLE_MUSIC] Exception while deserializing JSON response", e);
                                        return Mono.error(new RuntimeException("Exception while deserializing JSON response", e));
                                    }
                                });
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[APPLE_MUSIC] Exception while searching for song. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("[APPLE_MUSIC] Unexpected error during search", throwable);
                    return Mono.error(new RuntimeException("Unexpected error during song search", throwable));
                });
    }


    public Mono<AppleMusicFetchResponse> fetchSongById(String id) {
        return appleMusicWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(appleMusicRestConfigDto.getFetchMusicDetailEndpoint())
                        .queryParam("id", id)
                        .build())
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(jsonString -> {
                                    try {
                                        AppleMusicFetchResponse response = objectMapper.readValue(jsonString, AppleMusicFetchResponse.class);
                                        return Mono.just(response);
                                    } catch (Exception e) {
                                        log.error("[APPLE_MUSIC] Exception while deserializing JSON response", e);
                                        return Mono.error(new RuntimeException("Exception while deserializing JSON response", e));
                                    }
                                });
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[APPLE_MUSIC] Exception while fetching for song details. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("[APPLE_MUSIC] Unexpected error during fetch by id", throwable);
                    return Mono.error(new RuntimeException("Unexpected error during song fetch by id", throwable));
                });
    }
}
