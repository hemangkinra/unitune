package in.weekend.unitune.configuration.webclient;

import in.weekend.unitune.dto.webclient.SpotifyRestConfigDto;
import in.weekend.unitune.dto.external.spotify.SpotifySearchResponse;
import in.weekend.unitune.dto.external.spotify.SpotifyFetchResponse;
import in.weekend.unitune.dto.external.spotify.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@Configuration
public class SpotifyWebClientConfig {

    public static final String TOKEN_DELIMITER = ":";
    public static final String BEARER = "Bearer";
    public static final String BASIC = "Basic";
    public static final String FORWARD_SLASH = "/";

    public static final String SPACE = " ";
    private final WebClient spotifyWebClient;

    private final WebClient spotifyTokenWebClient;

    private final SpotifyRestConfigDto spotifyRestConfigDto;


    public SpotifyWebClientConfig(WebClient.Builder wcBuilder, SpotifyRestConfigDto spotifyRestConfigDto) {
        this.spotifyRestConfigDto = spotifyRestConfigDto;

        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3));

        this.spotifyWebClient = wcBuilder
                .baseUrl(spotifyRestConfigDto.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();

        this.spotifyTokenWebClient = wcBuilder
                .baseUrl(spotifyRestConfigDto.getTokenGenerationHost())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }


    public Mono<TokenResponse> generateAccessToken() {
        String clientCredentials = formClientCredentials();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");

        return spotifyTokenWebClient
                .post()
                .uri(spotifyRestConfigDto.getTokenGenerationEndpoint())
                .header(AUTHORIZATION, clientCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful())
                        return clientResponse.bodyToMono(TokenResponse.class);
                    else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("Exception while fetching token. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while fetching spotify token"));
                                });
                    }
                });

    }


    public Mono<SpotifySearchResponse> searchSong(String searchString, String bearerToken) {
        return spotifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(spotifyRestConfigDto.getSearchMusicEndpoint())
                        .queryParam("q", searchString)
                        .queryParam("type", "track")
                        .queryParam("limit", 1)
                        .build())
                .header(AUTHORIZATION, formBearerToken(bearerToken))
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(SpotifySearchResponse.class);
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[SPOTIFY] Exception while searching for song. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                });
    }


    public Mono<SpotifyFetchResponse> fetchSongBySpotifyId(String id, String bearerToken) {
        return spotifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(spotifyRestConfigDto.getFetchMusicDetailEndpoint())
                        .path(FORWARD_SLASH + id)
                        .build())
                .header(AUTHORIZATION, formBearerToken(bearerToken))
                .exchangeToMono(clientResponse -> {
                    if(clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(SpotifyFetchResponse.class);
                    } else {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponse -> {
                                    log.error("[SPOTIFY] Exception while fetching for song details. Status Code: {}, Response: {}", clientResponse.statusCode(), errorResponse);
                                    return Mono.error(new RuntimeException("Exception while searching for song"));
                                });
                    }
                });
    }


    private String formClientCredentials() {
        String tokenInPlainString = spotifyRestConfigDto.getClientId() + TOKEN_DELIMITER + spotifyRestConfigDto.getClientSecret();
        return BASIC + SPACE + Base64
                .getEncoder()
                .encodeToString(tokenInPlainString.getBytes(StandardCharsets.UTF_8));
    }

    private static String formBearerToken(String bearerToken) {
        return BEARER + SPACE + bearerToken;
    }

}
