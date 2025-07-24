package in.weekend.unitune.dto.webclient;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "external.http.spotify")
public class SpotifyRestConfigDto extends MusicPlatformRestConfigDto{
    String tokenGenerationHost;
    String tokenGenerationEndpoint;
    String clientId;
    String clientSecret;
}