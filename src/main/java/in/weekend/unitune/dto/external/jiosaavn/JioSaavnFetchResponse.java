package in.weekend.unitune.dto.external.jiosaavn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.dto.external.jiosaavn.common.Artist;
import in.weekend.unitune.dto.external.jiosaavn.common.Track;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.model.MusicMetadata;
import lombok.Data;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JioSaavnFetchResponse {
    private boolean success;
    private List<Track> data;

    public MusicMetadata formMusicMetaData() {
        MusicMetadata musicMetadata = new MusicMetadata();
        Track jioSaavnTrack = this.getData().getFirst();
        musicMetadata.setPlatformId(String.valueOf(jioSaavnTrack.getId()));
        musicMetadata.setTitle(jioSaavnTrack.getName());
        musicMetadata.setArtist(jioSaavnTrack.getArtists().getPrimary().getFirst().getName());
        musicMetadata.setArtists(jioSaavnTrack.getArtists().getAll().stream().map(Artist::getName).toList());
        musicMetadata.setAlbum(jioSaavnTrack.getAlbum().getName());
        musicMetadata.setUrl(jioSaavnTrack.getUrl());
        musicMetadata.setIsrc("Undefined");
        musicMetadata.setDuration(Duration.ofSeconds(jioSaavnTrack.getDuration()));
        musicMetadata.setThumbnail(jioSaavnTrack.getImage().getLast().getUrl());
        musicMetadata.setOriginalPlatform(PlatformType.JIO_SAAVN);
        return musicMetadata;
    }
}