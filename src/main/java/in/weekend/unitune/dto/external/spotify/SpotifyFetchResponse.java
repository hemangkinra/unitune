package in.weekend.unitune.dto.external.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.model.MusicMetadata;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpotifyFetchResponse {

    private Album album;

    private List<Artist> artists;

    @JsonProperty("available_markets")
    private List<String> availableMarkets;

    @JsonProperty("disc_number")
    private int discNumber;

    @JsonProperty("duration_ms")
    private int durationMs;

    private boolean explicit;

    @JsonProperty("external_ids")
    private ExternalIds externalIds;

    @JsonProperty("external_urls")
    private ExternalUrls externalUrls;

    private String href;
    private String id;

    @JsonProperty("is_local")
    private boolean isLocal;

    private String name;
    private int popularity;

    @JsonProperty("preview_url")
    private String previewUrl;

    @JsonProperty("track_number")
    private int trackNumber;

    private String type;
    private String uri;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Album {
        @JsonProperty("album_type")
        private String albumType;

        private List<Artist> artists;

        @JsonProperty("available_markets")
        private List<String> availableMarkets;

        @JsonProperty("external_urls")
        private ExternalUrls externalUrls;

        private String href;
        private String id;
        private List<Image> images;
        private String name;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("release_date_precision")
        private String releaseDatePrecision;

        @JsonProperty("total_tracks")
        private int totalTracks;

        private String type;
        private String uri;

        @JsonProperty("is_playable")
        private Boolean isPlayable;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Artist {

        @JsonProperty("external_urls")
        private ExternalUrls externalUrls;

        private String href;
        private String id;
        private String name;
        private String type;
        private String uri;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExternalUrls {
        private String spotify;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Image {
        private int height;
        private int width;
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExternalIds {
        private String isrc;
    }


    public MusicMetadata formMusicMetaData() {
        MusicMetadata musicMetadata = new MusicMetadata();
        musicMetadata.setPlatformId(String.valueOf(this.getId()));
        musicMetadata.setTitle(this.getName());
        musicMetadata.setArtist(this.getArtists().getFirst().getName());
        musicMetadata.setArtists(this.getArtists().stream().map(Artist::getName).toList());
        musicMetadata.setAlbum(this.getAlbum().getName());
        musicMetadata.setUrl(this.getExternalUrls().getSpotify());
        musicMetadata.setThumbnail(this.getAlbum().getImages().getFirst().getUrl());
        musicMetadata.setIsrc(this.getExternalIds().getIsrc());
        musicMetadata.setDuration(Duration.ofMillis(this.getDurationMs()));
        musicMetadata.setPreviewUrl(this.getPreviewUrl());
        musicMetadata.setOriginalPlatform(PlatformType.SPOTIFY);
        return musicMetadata;
    }
}
