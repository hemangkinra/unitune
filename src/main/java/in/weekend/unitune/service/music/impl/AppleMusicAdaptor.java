package in.weekend.unitune.service.music.impl;

import in.weekend.unitune.configuration.webclient.AppleMusicWebClientConfig;
import in.weekend.unitune.dto.external.applemusic.AppleMusicFetchResponse;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.exceptions.MalformedUrlException;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.PlatformLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AppleMusicAdaptor extends BaseMusicPlatformAdaptor {

    @Autowired
    AppleMusicWebClientConfig webClientConfig;

    @Override
    public Mono<MusicMetadata> extractMetadata(String url) {
        String id = extractSongId(url);
        log.info("id: {}", id);
        return webClientConfig.fetchSongById(id)
                .map(AppleMusicFetchResponse::formMusicMetaData)
                .doOnNext(metadata -> log.debug("[APPLE_MUSIC] metadata: {}", metadata));
    }

    @Override
    public Mono<PlatformLink> searchAndGeneratePlatformLink(MusicMetadata metadata) {
        return webClientConfig.searchSong(getSearchString(metadata))
                .map(appleMusicSearchResponse -> {
                    PlatformLink platformLink = appleMusicSearchResponse.formPlatformLink();
                    platformLink.setAvailable(isAvailable());
                    platformLink.setPlatform(getPlatformType());
                    return platformLink;
                });
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.APPLE_MUSIC;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String extractSongId(String url) {
        // Pattern for album URLs with song parameter (?i=songId)
        Pattern albumPattern = Pattern.compile("/album/[^/]+/[0-9]+\\?i=([0-9]+)");

        // Pattern for direct song URLs
        Pattern songPattern = Pattern.compile("/song/[^/]+/([0-9]+)");

        // First try to match album URLs with song parameter
        Matcher albumMatcher = albumPattern.matcher(url);
        if (albumMatcher.find()) {
            return albumMatcher.group(1);
        }

        Matcher songMatcher = songPattern.matcher(url);
        if (songMatcher.find()) {
            return songMatcher.group(1);
        }

        throw new MalformedUrlException("Unable to identify id");
    }
}
