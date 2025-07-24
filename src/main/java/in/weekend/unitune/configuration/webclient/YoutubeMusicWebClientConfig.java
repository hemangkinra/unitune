package in.weekend.unitune.configuration.webclient;

import in.weekend.unitune.dto.external.youtubemusic.YoutubeMusicFetchResponse;
import in.weekend.unitune.dto.external.youtubemusic.YoutubeMusicSearchResponse;
import in.weekend.unitune.dto.webclient.YoutubeMusicRestConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
public class YoutubeMusicWebClientConfig {

    public static final String FORWARD_SLASH = "/";

    private final WebClient youtubeMusicWebClient;

    private final YoutubeMusicRestConfigDto youtubeMusicRestConfigDto;


    public YoutubeMusicWebClientConfig(WebClient.Builder wcBuilder, YoutubeMusicRestConfigDto youtubeMusicRestConfigDto) {
        this.youtubeMusicRestConfigDto = youtubeMusicRestConfigDto;

        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3));

        this.youtubeMusicWebClient = wcBuilder
                .baseUrl(youtubeMusicRestConfigDto.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }


    public Mono<YoutubeMusicSearchResponse> searchSong(String searchString) {
        return youtubeMusicWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(youtubeMusicRestConfigDto.getSearchMusicEndpoint())
                        .queryParam("query", searchString)
                        .queryParam("limit", 1)
                        .queryParam("filter_type", "songs")
                        .build())
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(YoutubeMusicSearchResponse.class);
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[YOUTUBE_MUSIC] Exception while searching for song. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("[YOUTUBE_MUSIC] Unexpected error during search", throwable);
                    return Mono.error(new RuntimeException("Unexpected error during song search", throwable));
                });
    }


    public Mono<YoutubeMusicFetchResponse> fetchSongById(String id) {
        return youtubeMusicWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(youtubeMusicRestConfigDto.getFetchMusicDetailEndpoint())
                        .path(FORWARD_SLASH + id)
                        .build())
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(YoutubeMusicFetchResponse.class);
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[YOUTUBE_MUSIC] Exception while fetching for song details. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("[YOUTUBE_MUSIC] Unexpected error during fetch by id", throwable);
                    return Mono.error(new RuntimeException("Unexpected error during song fetch by id", throwable));
                });
    }
}
