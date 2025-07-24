package in.weekend.unitune.dto.external.youtubemusic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.weekend.unitune.model.PlatformLink;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YoutubeMusicSearchResponse {
    private String query;
    private String filter;
    private Integer limit;
    private Integer count;
    private List<Result> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Result {
        private String category;
        private String resultType;
        private String title;
        private Album album;
        private Boolean inLibrary;
        private FeedbackTokens feedbackTokens;
        private String videoId;
        private String videoType;
        private String duration;
        private String year;
        private List<Artist> artists;
        private Integer duration_seconds;
        private String views;
        private Boolean isExplicit;
        private List<Thumbnail> thumbnails;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Album {
            private String name;
            private String id;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class FeedbackTokens {
            private String add;
            private String remove;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Artist {
            private String name;
            private String id;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Thumbnail {
            private String url;
            private Integer width;
            private Integer height;
        }
    }


    public PlatformLink formPlatformLink() {
        PlatformLink platformLink = new PlatformLink();
        Result result = this.getResults().getFirst();
        platformLink.setPlatformId(result.getVideoId());
        platformLink.setUrl(generateYoutubeMusicUrl(result.getVideoId()));
        platformLink.setPlatformSpecificData(this.toString());
        return platformLink;
    }

    public static String generateYoutubeMusicUrl(String videoId) {
        return "https://music.youtube.com/watch?v=" + videoId;
    }
}
