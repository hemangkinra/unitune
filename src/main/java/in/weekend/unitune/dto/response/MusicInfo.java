package in.weekend.unitune.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.model.MusicMetadata;
import lombok.Data;

import java.time.Duration;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MusicInfo {
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String imageUrl;
    private String genre;
    private String releaseDate;
    private String previewUrl;

    public static MusicInfo fromMusicMetadata(MusicMetadata metadata) {
        MusicInfo info = new MusicInfo();
        info.setTitle(metadata.getTitle());
        info.setAlbum(metadata.getAlbum());
        info.setImageUrl(metadata.getThumbnail());
        info.setPreviewUrl(metadata.getPreviewUrl());

        if (metadata.getDuration() != null) {
            info.setDuration(formatDuration(metadata.getDuration()));
        }

        return info;
    }

    private static String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
