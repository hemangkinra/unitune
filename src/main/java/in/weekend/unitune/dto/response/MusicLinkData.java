package in.weekend.unitune.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.libs.StringUtils;
import in.weekend.unitune.model.UniversalMusicLink;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MusicLinkData {
    private String id;
    private String contentType;
    private MusicInfo musicInfo;
    private String originalUrl;
    private String originalPlatform;
    private List<PlatformLinkInfo> platforms;
    private int totalPlatforms;
    private int availablePlatforms;
    private String createdAt;
    private String lastUpdated;

    public static MusicLinkData fromUniversalMusicLink(UniversalMusicLink universalLink) {
        MusicLinkData data = new MusicLinkData();
        data.setId(StringUtils.encodeToBase62(universalLink.getUmlId()));
        data.setContentType(universalLink.getContentType().toString());
        data.setOriginalUrl(universalLink.getOriginalUrl());
        data.setOriginalPlatform(universalLink.getOriginalPlatform().getDisplayName());
        data.setCreatedAt(new Date(universalLink.getCreatedAt()).toString());
        data.setLastUpdated(new Date(universalLink.getLastUpdated()).toString());

        // Convert metadata
        if (universalLink.getMetadata() != null) {
            data.setMusicInfo(MusicInfo.fromMusicMetadata(universalLink.getMetadata()));
        }

        // Convert platform links
        List<PlatformLinkInfo> platformInfos = universalLink.getPlatformLinks().stream()
                .map(PlatformLinkInfo::fromPlatformLink)
                .collect(Collectors.toList());
        data.setPlatforms(platformInfos);
        data.setTotalPlatforms(platformInfos.size());
        data.setAvailablePlatforms((int) platformInfos.stream().filter(PlatformLinkInfo::isAvailable).count());

        return data;
    }
}