package in.weekend.unitune.model;

import in.weekend.unitune.enums.PlatformType;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
public class MusicMetadata {
    private String platformId;
    private String title;
    private String artist;
    private List<String> artists;
    private String album;
    private String url;
    private String isrc;
    private String thumbnail;
    private String previewUrl;
    private Duration duration;
    private PlatformType originalPlatform;
}
