package in.weekend.unitune.service.music.impl;

import in.weekend.unitune.configuration.webclient.JioSaavnWebClientConfig;
import in.weekend.unitune.dto.external.jiosaavn.JioSaavnFetchResponse;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.libs.StringUtils;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.PlatformLink;
import in.weekend.unitune.model.UniversalMusicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class JioSaavnAdaptor extends BaseMusicPlatformAdaptor {

    @Autowired
    private JioSaavnWebClientConfig jioSaavnWebClientConfig;

    @Override
    public Mono<MusicMetadata> extractMetadata(String url) {
        return jioSaavnWebClientConfig.fetchSongByUrl(url)
                .map(JioSaavnFetchResponse::formMusicMetaData)
                .doOnNext(metadata -> log.debug("[JIO_SAAVN] metadata: {}", metadata));

    }

    @Override
    public Mono<PlatformLink> searchAndGeneratePlatformLink(MusicMetadata metadata) {
        return jioSaavnWebClientConfig.searchSong(getSearchString(metadata))
                .map(jioSaavnSearchResponse -> {
                    PlatformLink platformLink = jioSaavnSearchResponse.formPlatformLink();
                    platformLink.setPlatform(getPlatformType());
                    platformLink.setAvailable(isAvailable());
                    return platformLink;
                });
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.JIO_SAAVN;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public Mono<UniversalMusicLink> checkInDBCache(String url) {
        PlatformType platformType = getPlatformType();
        String cleanUrl = StringUtils.normalizeUrl(url);
        Query query = new Query(Criteria.where("platformLinks")
                .elemMatch(Criteria.where("platform").is(platformType)
                        .and("url").is(cleanUrl)));

        return universalMusicLinkRepository.find(query).next();
    }

    @Override
    public String extractSongId(String url) {
        return "";
    }
}
