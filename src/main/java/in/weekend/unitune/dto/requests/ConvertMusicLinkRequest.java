package in.weekend.unitune.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConvertMusicLinkRequest {
    private String url;
    private List<String> targetPlatforms;
    private boolean includeMetadata;
    private boolean resetCache = false;
}
