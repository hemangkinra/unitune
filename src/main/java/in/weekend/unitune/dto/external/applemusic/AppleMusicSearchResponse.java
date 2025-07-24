package in.weekend.unitune.dto.external.applemusic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.model.PlatformLink;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppleMusicSearchResponse {
    private int resultCount;
    private List<AppleMusicTrack> results;

    public PlatformLink formPlatformLink() {
        PlatformLink platformLink = new PlatformLink();
        AppleMusicTrack appleMusicTrack = this.getResults().getFirst();
        platformLink.setPlatformId(String.valueOf(appleMusicTrack.getTrackId()));
        platformLink.setUrl(appleMusicTrack.getTrackViewUrl());
        platformLink.setPlatformSpecificData(this.toString());
        platformLink.setPreviewUrl(appleMusicTrack.getPreviewUrl());
        return platformLink;
    }
}

