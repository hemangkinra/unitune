package in.weekend.unitune.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.model.PlatformLink;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformLinkInfo {
    private String platform;
    private String url;
    private boolean available;
    private String errorMessage;

    public static PlatformLinkInfo fromPlatformLink(PlatformLink platformLink) {
        PlatformLinkInfo platformLinkInfo = new PlatformLinkInfo();
        platformLinkInfo.setPlatform(platformLink.getPlatform().getDisplayName());
        platformLinkInfo.setUrl(platformLink.getUrl());
        platformLinkInfo.setAvailable(platformLink.isAvailable());
        platformLinkInfo.setErrorMessage(platformLinkInfo.getErrorMessage());
        return platformLinkInfo;
    }
}