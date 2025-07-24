package in.weekend.unitune.dto.external.jiosaavn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.dto.external.jiosaavn.common.Track;
import in.weekend.unitune.model.PlatformLink;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JioSaavnSearchResponse {
    private boolean success;
    private DataPayload data;

    @Data
    public static class DataPayload {
        private int total;
        private int start;
        private List<Track> results;
    }

    public PlatformLink formPlatformLink() {
        PlatformLink platformLink = new PlatformLink();
        Track track = this.getData().getResults().getFirst();
        platformLink.setPlatformId(track.getId());
        platformLink.setUrl(track.getUrl());
        platformLink.setPlatformSpecificData(this.toString());
        platformLink.setPreviewUrl(track.getDownloadUrl().getLast().getUrl());
        return platformLink;
    }
}

