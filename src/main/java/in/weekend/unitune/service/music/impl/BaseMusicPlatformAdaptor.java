package in.weekend.unitune.service.music.impl;

import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.model.MusicMetadata;
import in.weekend.unitune.model.UniversalMusicLink;
import in.weekend.unitune.repository.UniversalMusicLinkRepository;
import in.weekend.unitune.service.music.MusicPlatformAdaptor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

@Data
public abstract class BaseMusicPlatformAdaptor implements MusicPlatformAdaptor {

    @Autowired
    protected UniversalMusicLinkRepository universalMusicLinkRepository;


    @Override
    public boolean canHandle(String url) {
        return url.contains(getPlatformType().getDomain());
    }

    @Override
    public boolean canHandle(PlatformType platform) {
        return platform.equals(getPlatformType());
    }

    @Override
    public Mono<UniversalMusicLink> checkInDBCache(String url) {
        PlatformType platformType = getPlatformType();
        String id = extractSongId(url);
        Query query = new Query(Criteria.where("platformLinks")
                .elemMatch(Criteria.where("platform").is(platformType)
                        .and("platformId").is(id)));

        return universalMusicLinkRepository.find(query).next();
    }

    public String getSearchString(MusicMetadata metadata) {
        return metadata.getTitle() + " " + metadata.getArtist();
    }

    @Override
    public abstract String extractSongId(String url);

    @Override
    public abstract PlatformType getPlatformType();

}
