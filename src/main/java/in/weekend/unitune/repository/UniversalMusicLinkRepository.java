package in.weekend.unitune.repository;

import in.weekend.unitune.configuration.CollectionConfigs;
import in.weekend.unitune.model.UniversalMusicLink;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UniversalMusicLinkRepository extends AbstractMongoRepository<UniversalMusicLink> {
    protected UniversalMusicLinkRepository(ReactiveMongoTemplate mongoTemplate, CollectionConfigs collectionConfigs) {
        super(mongoTemplate, UniversalMusicLink.class, collectionConfigs.getCollectionName(UniversalMusicLink.class));
    }
}
