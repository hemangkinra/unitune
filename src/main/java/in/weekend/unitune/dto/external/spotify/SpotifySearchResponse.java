package in.weekend.unitune.dto.external.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.weekend.unitune.model.PlatformLink;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpotifySearchResponse {

    private Tracks tracks;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tracks {
        private String href;
        private int limit;
        private String next;
        private int offset;
        private String previous;
        private int total;
        private List<Item> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
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

        @JsonProperty("is_playable")
        private boolean isPlayable;

        private String name;
        private int popularity;

        @JsonProperty("preview_url")
        private String previewUrl;

        @JsonProperty("track_number")
        private int trackNumber;

        private String type;
        private String uri;
    }

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

        @JsonProperty("is_playable")
        private boolean isPlayable;

        private String name;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("release_date_precision")
        private String releaseDatePrecision;

        @JsonProperty("total_tracks")
        private int totalTracks;

        private String type;
        private String uri;
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


    public PlatformLink formPlatformLink() {
        PlatformLink platformLink = new PlatformLink();
        Item spotifyTrackItem = this.getTracks().getItems().getFirst();
        platformLink.setPlatformId(String.valueOf(spotifyTrackItem.getId()));
        platformLink.setUrl(spotifyTrackItem.getExternalUrls().getSpotify());
        platformLink.setPlatformSpecificData(this.toString());
        platformLink.setPreviewUrl(spotifyTrackItem.getPreviewUrl());
        return platformLink;
    }
}