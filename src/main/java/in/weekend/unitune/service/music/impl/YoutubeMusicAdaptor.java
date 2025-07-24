package in.weekend.unitune.service.music.impl;

import in.weekend.unitune.configuration.webclient.YoutubeMusicWebClientConfig;
import in.weekend.unitune.dto.external.youtubemusic.YoutubeMusicFetchResponse;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.exceptions.MalformedUrlException;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.PlatformLink;
import in.weekend.unitune.model.UniversalMusicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class YoutubeMusicAdaptor extends BaseMusicPlatformAdaptor {

    private static final Pattern YOUTUBE_MUSIC_SONG_PATTERN = Pattern.compile("[?&]v=([\\w-]+)");

    @Autowired
    private YoutubeMusicWebClientConfig webClientConfig;

    @Override
    public Mono<MusicMetadata> extractMetadata(String url) {
        return webClientConfig.fetchSongById(extractSongId(url))
                .map(YoutubeMusicFetchResponse::formMusicMetaData)
                .doOnNext(metadata -> log.debug("[YOUTUBE_MUSIC] metadata: {}", metadata));
    }

    @Override
    public Mono<PlatformLink> searchAndGeneratePlatformLink(MusicMetadata metadata) {
        return webClientConfig.searchSong(getSearchString(metadata))
                .map(youtubeMusicSearchResponse -> {
                    PlatformLink platformLink = youtubeMusicSearchResponse.formPlatformLink();
                    platformLink.setAvailable(isAvailable());
                    platformLink.setPlatform(getPlatformType());
                    return platformLink;
                });
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.YOUTUBE_MUSIC;
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
        Matcher songMatcher = YOUTUBE_MUSIC_SONG_PATTERN.matcher(url);
        log.info("songMatcher: {}", songMatcher);
        if (songMatcher.find()) {
            log.info("songMatcher: {}", songMatcher);
            return songMatcher.group(1);
        }
        throw new MalformedUrlException("Unable to identify song ID from URL: " + url);
    }
}
