package in.weekend.unitune.repository;

import in.weekend.unitune.configuration.RedisTemplateFactory;
import in.weekend.unitune.dto.external.spotify.TokenResponse;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SpotifyTokenRedisRepository extends AbstractRedisRepository<TokenResponse> {

    protected SpotifyTokenRedisRepository(RedisTemplateFactory redisTemplate) {
        super(redisTemplate.createTemplate(TokenResponse.class), TokenResponse.class, "spotify");
    }
}
