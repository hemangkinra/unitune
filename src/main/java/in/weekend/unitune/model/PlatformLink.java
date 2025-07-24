package in.weekend.unitune.model;

import in.weekend.unitune.enums.PlatformType;
import lombok.Data;

@Data
public class PlatformLink {
    private String platformId;
    private PlatformType platform;
    private String url;
    private boolean available;
    private String errorMessage;
    private long lastUpdated;
    private String platformSpecificData;
    private String previewUrl;

    public PlatformLink() {

    }


    public PlatformLink(PlatformType platform, String url) {
        this.platform = platform;
        this.url = url;
        this.available = true;
        this.lastUpdated = System.currentTimeMillis();
    }

    public PlatformLink(PlatformType platform, Throwable err) {
        this.platform = platform;
        this.available = false;
        this.errorMessage = err.getMessage();
        this.lastUpdated = System.currentTimeMillis();
    }

    public static PlatformLink from(MusicMetadata musicMetadata) {
        PlatformLink platformLink = new PlatformLink(musicMetadata.getOriginalPlatform(), musicMetadata.getUrl());
        platformLink.setPlatformId(musicMetadata.getPlatformId());
        return platformLink;
    }
}