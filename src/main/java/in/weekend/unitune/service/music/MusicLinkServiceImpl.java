package in.weekend.unitune.service.music;

import in.weekend.unitune.dto.requests.ConvertMusicLinkRequest;
import in.weekend.unitune.dto.response.MusicLinkData;
import in.weekend.unitune.dto.response.MusicLinkResponse;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.libs.StringUtils;
import in.weekend.unitune.repository.UniversalMusicLinkRepository;
import in.weekend.unitune.service.PlatformDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class MusicLinkServiceImpl implements MusicLinkService {

    @Autowired
    private PlatformDetector platformDetector;

    @Autowired
    private LinkResolver linkResolver;

    @Autowired
    private UniversalMusicLinkRepository universalMusicLinkRepository;

    @Override
    public Mono<MusicLinkResponse> convertToUniversal(ConvertMusicLinkRequest request) {
        try {
            PlatformType sourcePlatform = platformDetector.detectPlatform(request.getUrl());

            return linkResolver.resolveAndCache(request.getUrl(), sourcePlatform, request.isResetCache())
                    .map(universalMusicLink -> {
                        MusicLinkData data = MusicLinkData.fromUniversalMusicLink(universalMusicLink);
                        return MusicLinkResponse.success(data);
                    })
                    .onErrorResume(e -> {
                        log.error("Error while resolving link", e);
                        return Mono.just(MusicLinkResponse.error("Failed to convert link: " + e.getMessage()));
                    });

        } catch (Exception e) {
            log.error("Failed to convert music link: {}", e.getMessage());
            return Mono.just(MusicLinkResponse.error("Failed to convert link: " + e.getMessage()));
        }
    }

    @Override
    public Mono<MusicLinkResponse> fetchMusicLinkResponse(String uniTuneId) {
        Query query = new Query(Criteria.where("umlId").is(StringUtils.decodeFromBase62(uniTuneId)));
        return universalMusicLinkRepository
                .find(query)
                .next()
                .map(universalMusicLink -> {
                    MusicLinkData data = MusicLinkData.fromUniversalMusicLink(universalMusicLink);
                    return MusicLinkResponse.success(data);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("No Existing Data in UniversalMusicLink Collection");
                    return Mono.just(MusicLinkResponse.error("No Data Found"));
                }))
                .onErrorResume(e -> {
                    log.error("Error while resolving link", e);
                    return Mono.just(MusicLinkResponse.error("Failed to convert link: " + e.getMessage()));
                });
    }
}
