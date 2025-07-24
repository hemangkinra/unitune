package in.weekend.unitune.model;


import in.weekend.unitune.enums.ContentType;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.libs.Snowflake;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UniversalMusicLink {
    @Id
    private String id;
    private Long umlId;
    private ContentType contentType;
    private MusicMetadata metadata;
    private String originalUrl;
    private PlatformType originalPlatform;
    private List<PlatformLink> platformLinks;
    private long createdAt;
    private long lastUpdated;
    private Map<String, Object> additionalData;

    public UniversalMusicLink() {
        this.umlId = Snowflake.generateId();
        this.platformLinks = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
        this.additionalData = new HashMap<>();
    }

    public UniversalMusicLink(String originalUrl, PlatformType originalPlatform, ContentType contentType) {
        this();
        this.originalUrl = originalUrl;
        this.originalPlatform = originalPlatform;
        this.contentType = contentType;
    }

    public UniversalMusicLink(UniversalMusicLink uml, MusicMetadata metadata, ContentType contentType) {
        this(metadata.getUrl(), metadata.getOriginalPlatform(), contentType);
        this.platformLinks.add(PlatformLink.from(metadata));
        this.metadata = metadata;
        if (uml != null) {
            this.id = uml.getId();
            this.umlId = uml.getUmlId();
            this.createdAt = uml.getCreatedAt();
        }
    }
}
