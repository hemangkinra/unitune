package in.weekend.unitune.service.music;


import in.weekend.unitune.enums.ContentType;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.exceptions.PlatformException;
import in.weekend.unitune.exceptions.ResolutionException;
import in.weekend.unitune.libs.StringUtils;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.PlatformLink;
import in.weekend.unitune.model.UniversalMusicLink;
import in.weekend.unitune.repository.UniversalMusicLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LinkResolver {


    public static final UniversalMusicLink NULL_AS_EMPTY_UNIVERSAL_LINK = null;
    @Autowired
    private List<MusicPlatformAdaptor> platformAdaptors;

    @Autowired
    private UniversalMusicLinkRepository universalMusicLinkRepository;


    public Mono<UniversalMusicLink> resolveAndCache(String originalUrl, PlatformType sourcePlatform, Boolean resetCache) {
        MusicPlatformAdaptor sourceAdapter = findAdapter(sourcePlatform);
        log.info("platformAdaptors size: {}", platformAdaptors.size());
        return sourceAdapter.checkInDBCache(originalUrl)
                .flatMap(universalMusicLink -> {
                    log.info("universalMusicLink: {}", universalMusicLink);
                    if (Boolean.TRUE.equals(resetCache)) {
                        return cacheMissResolveMusic(originalUrl, sourceAdapter, universalMusicLink);
                    }
                    return Mono.just(universalMusicLink);
                })
                .switchIfEmpty(Mono.defer(() -> cacheMissResolveMusic(originalUrl, sourceAdapter, NULL_AS_EMPTY_UNIVERSAL_LINK)))
                .onErrorMap(e -> new ResolutionException("Failed to resolve music link: " + e.getMessage()));
    }

    private Mono<UniversalMusicLink> cacheMissResolveMusic(String originalUrl, MusicPlatformAdaptor sourceAdapter, UniversalMusicLink dbUml) {
        log.debug("Cache Miss for Music Resolution, calling each service");
        return sourceAdapter
                .extractMetadata(originalUrl)
                .doOnError(e -> log.error("Error in extractMetadata: ", e))
                .map(metadata -> new UniversalMusicLink(dbUml, metadata, ContentType.TRACK))
                .flatMap(universalLink -> generateAllPlatformLinks(universalLink, universalLink.getMetadata()))
                .doOnError(e -> log.error("Error in generateAllPlatformLinks: ", e))
                .doOnSuccess(universalMusicLink ->
                        universalMusicLinkRepository.save(universalMusicLink)
                                .doOnError(e -> log.warn("Failed to save to cache: ", e))
                                .subscribe()
                );
    }


    private MusicPlatformAdaptor findAdapter(PlatformType platform) throws ResolutionException {
        return platformAdaptors.stream()
                .filter(adapter -> adapter.canHandle(platform))
                .findFirst()
                .orElseThrow(() -> new ResolutionException("No adapter found for platform: " + platform));
    }

    private Mono<UniversalMusicLink> generateAllPlatformLinks(UniversalMusicLink universalLink, MusicMetadata metadata) throws PlatformException {
        List<Mono<PlatformLink>> platformLinkMonos = platformAdaptors.stream()
                .filter(adapter -> adapter.getPlatformType() != universalLink.getOriginalPlatform())
                .map(adapter -> generatePlatformLink(adapter, metadata))
                .collect(Collectors.toList());

        return Flux.merge(platformLinkMonos)
                .doOnNext(platformLink -> {
                    log.info("Platform: {}", platformLink.getPlatform());
                    log.info("Platform Preview Url: {}", platformLink.getPreviewUrl());
                    universalLink.getPlatformLinks().add(platformLink);
                    if(StringUtils.isNotEmpty(platformLink.getPreviewUrl())){
                        universalLink.getMetadata().setPreviewUrl(platformLink.getPreviewUrl());
                    }
                })
                .then(Mono.just(universalLink));
    }


    private Mono<PlatformLink> generatePlatformLink(MusicPlatformAdaptor adapter, MusicMetadata metadata) throws PlatformException {
        return adapter.searchAndGeneratePlatformLink(metadata)
                .doOnNext(platformLink -> log.debug("Generated link for {}: {}",
                        adapter.getPlatformType(), platformLink.getUrl()))
                .doOnError(err -> log.warn("Failed to generate link for {}: {}",
                        adapter.getPlatformType(), err.getMessage()))
                .onErrorResume(err ->
                        Mono.just(new PlatformLink(adapter.getPlatformType(), err)));
    }
}
