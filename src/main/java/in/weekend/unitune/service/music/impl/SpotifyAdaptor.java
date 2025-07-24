package in.weekend.unitune.service.music.impl;

import in.weekend.unitune.configuration.webclient.SpotifyWebClientConfig;
import in.weekend.unitune.dto.external.spotify.SpotifyFetchResponse;
import in.weekend.unitune.dto.external.spotify.TokenResponse;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.exceptions.MalformedUrlException;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.PlatformLink;
import in.weekend.unitune.repository.SpotifyTokenRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SpotifyAdaptor extends BaseMusicPlatformAdaptor {

    private static final Pattern SPOTIFY_SONG_PATTERN = Pattern.compile("/track/([^/?#]+)");

    @Autowired
    private SpotifyTokenRedisRepository spotifyTokenRedisRepository;

    @Autowired
    private SpotifyWebClientConfig webClientConfig;

    @Override
    public Mono<MusicMetadata> extractMetadata(String url) {
        log.info("fetching spotify");
        return getSpotifyToken()
                .flatMap(token -> webClientConfig.fetchSongBySpotifyId(extractSongId(url), token))
                .map(SpotifyFetchResponse::formMusicMetaData)
                .doOnNext(metadata -> log.debug("[SPOTIFY] metadata: {}", metadata));
    }

    @Override
    public Mono<PlatformLink> searchAndGeneratePlatformLink(MusicMetadata metadata) {
        return getSpotifyToken()
                .flatMap(token -> webClientConfig.searchSong(getSearchString(metadata), token))
                .map(spotifySearchResponse -> {
                    PlatformLink platformLink = spotifySearchResponse.formPlatformLink();
                    platformLink.setPlatform(getPlatformType());
                    platformLink.setAvailable(isAvailable());
                    return platformLink;
                });
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.SPOTIFY;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String extractSongId(String url) {
        if (url == null) {
            throw new IllegalArgumentException("URL must not be null");
        }
        Matcher songMatcher = SPOTIFY_SONG_PATTERN.matcher(url);
        if (songMatcher.find()) {
            return songMatcher.group(1);
        }
        throw new MalformedUrlException("Unable to identify song ID from URL: " + url);
    }

    private Mono<String> getSpotifyToken() {
        return spotifyTokenRedisRepository.findById("token")
                .doOnError(e -> log.error("error occurred at redis fetch or token generation", e))
                .switchIfEmpty(Mono.defer(this::handleCacheMiss))
                .map(TokenResponse::getAccessToken);
    }

    private Mono<TokenResponse> handleCacheMiss() {
        return webClientConfig
                .generateAccessToken()
                .doOnNext(token -> {
                    spotifyTokenRedisRepository
                            .save("token", token, Duration.ofSeconds(3000))
                            .subscribe();
                })
                .doOnError(e -> log.error("error at token generation", e));
    }
}
