package in.weekend.unitune.dto.webclient;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "external.http.jio-saavn")
public class JioSaavnRestConfigDto extends MusicPlatformRestConfigDto{
}
