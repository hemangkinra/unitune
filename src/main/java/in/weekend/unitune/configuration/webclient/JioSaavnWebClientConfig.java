package in.weekend.unitune.configuration.webclient;

import in.weekend.unitune.dto.external.jiosaavn.JioSaavnFetchResponse;
import in.weekend.unitune.dto.external.jiosaavn.JioSaavnSearchResponse;
import in.weekend.unitune.dto.webclient.JioSaavnRestConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Slf4j
@Configuration
public class JioSaavnWebClientConfig {

    private final WebClient jiosaavnWebClient;

    private final JioSaavnRestConfigDto jioSaavnRestConfigDto;


    public JioSaavnWebClientConfig(WebClient.Builder wcBuilder, JioSaavnRestConfigDto jioSaavnRestConfigDto) {
        this.jioSaavnRestConfigDto = jioSaavnRestConfigDto;

        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3));

        this.jiosaavnWebClient = wcBuilder
                .baseUrl(jioSaavnRestConfigDto.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }


    public Mono<JioSaavnSearchResponse> searchSong(String searchString) {
        return jiosaavnWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(jioSaavnRestConfigDto.getSearchMusicEndpoint())
                        .queryParam("query", searchString)
                        .build())
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(JioSaavnSearchResponse.class);
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[JIO_SAAVN] Exception while searching for song. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("[JIO_SAAVN] Unexpected error during search", throwable);
                    return Mono.error(new RuntimeException("Unexpected error during song search", throwable));
                });
    }


    public Mono<JioSaavnFetchResponse> fetchSongByUrl(String url) {
        return jiosaavnWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(jioSaavnRestConfigDto.getFetchMusicDetailEndpoint())
                        .queryParam("link", url)
                        .build())
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(JioSaavnFetchResponse.class);
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[JIO_SAAVN] Exception while fetching for song details. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("[JIO_SAAVN] Unexpected error during fetch by id", throwable);
                    return Mono.error(new RuntimeException("Unexpected error during song fetch by id", throwable));
                });
    }

}
