package in.weekend.unitune.dto.external.applemusic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.model.MusicMetadata;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppleMusicFetchResponse {
    private int resultCount;
    private List<AppleMusicTrack> results;

    public MusicMetadata formMusicMetaData(){
        MusicMetadata musicMetadata = new MusicMetadata();
        AppleMusicTrack appleMusicTrack = this.getResults().getFirst();
        musicMetadata.setPlatformId(String.valueOf(appleMusicTrack.getTrackId()));
        musicMetadata.setTitle(appleMusicTrack.getTrackName());
        extractArtists(appleMusicTrack, musicMetadata);
        musicMetadata.setArtist(musicMetadata.getArtists().getFirst());
        musicMetadata.setAlbum(appleMusicTrack.getCollectionName());
        musicMetadata.setUrl(appleMusicTrack.getTrackViewUrl());
        musicMetadata.setIsrc("Undefined");
        musicMetadata.setDuration(Duration.ofMillis(appleMusicTrack.getTrackTimeMillis()));
        musicMetadata.setThumbnail(upscaleArtworkUrl(appleMusicTrack.getArtworkUrl100()));
        musicMetadata.setOriginalPlatform(PlatformType.APPLE_MUSIC);
        return musicMetadata;
    }

    private static void extractArtists(AppleMusicTrack appleMusicTrack, MusicMetadata musicMetadata) {
        String[] artistArray = appleMusicTrack.getArtistName().split("\\s*,\\s*|\\s*&\\s*");
        List<String> artistList = new ArrayList<>(Arrays.asList(artistArray));
        musicMetadata.setArtists(artistList);
    }

    public String upscaleArtworkUrl(String url) {
        return url.replace("/100x100", "/600x600");
    }
}
