package in.weekend.unitune.dto.external.youtubemusic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.dto.external.applemusic.AppleMusicTrack;
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
public class YoutubeMusicFetchResponse {
    private String videoId;
    private String title;
    private String lengthSeconds;
    private String channelId;
    private boolean isOwnerViewing;
    private boolean isCrawlable;
    private Thumbnail thumbnail;
    private boolean allowRatings;
    private String viewCount;
    private String author;
    private boolean isPrivate;
    private boolean isUnpluggedCorpus;
    private String musicVideoType;
    private boolean isLiveContent;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Thumbnail {
        private List<ThumbnailEntry> thumbnails;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class ThumbnailEntry {
            private String url;
            private int width;
            private int height;
        }
    }


    public MusicMetadata formMusicMetaData(){
        MusicMetadata musicMetadata = new MusicMetadata();
        musicMetadata.setPlatformId(String.valueOf(this.getVideoId()));
        musicMetadata.setTitle(this.getTitle());
        extractArtists(this, musicMetadata);
        musicMetadata.setArtist(musicMetadata.getArtists().getFirst());
        musicMetadata.setUrl(generateYoutubeMusicUrl(this.getVideoId()));
        musicMetadata.setDuration(Duration.ofSeconds(Long.parseLong(this.getLengthSeconds())));
        musicMetadata.setThumbnail(this.getThumbnail().getThumbnails().getLast().getUrl());
        musicMetadata.setOriginalPlatform(PlatformType.YOUTUBE_MUSIC);
        return musicMetadata;
    }

    private static void extractArtists(YoutubeMusicFetchResponse youtubeMusicFetchResponse, MusicMetadata musicMetadata) {
        String[] artistArray = youtubeMusicFetchResponse.getAuthor().split(",\\s*");
        List<String> artistList = new ArrayList<>(Arrays.asList(artistArray));
        musicMetadata.setArtists(artistList);
    }

    public static String generateYoutubeMusicUrl(String videoId){
        return "https://music.youtube.com/watch?v="+videoId;
    }
}

