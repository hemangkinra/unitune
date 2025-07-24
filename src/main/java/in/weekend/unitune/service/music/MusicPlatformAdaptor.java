package in.weekend.unitune.service.music;

import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.PlatformLink;
import in.weekend.unitune.model.UniversalMusicLink;
import reactor.core.publisher.Mono;

public interface MusicPlatformAdaptor {
    boolean canHandle(String url);
    boolean canHandle(PlatformType platform);

    Mono<MusicMetadata> extractMetadata(String url);
    Mono<PlatformLink> searchAndGeneratePlatformLink(MusicMetadata metadata);

    PlatformType getPlatformType();
    boolean isAvailable();

    Mono<UniversalMusicLink> checkInDBCache(String url);

    String extractSongId(String url);

}
