package in.weekend.unitune.dto.external.jiosaavn.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Track {
    private String id;
    private String name;
    private String type;
    private String year;
    private String releaseDate;
    private int duration;
    private String label;
    private boolean explicitContent;
    private int playCount;
    private String language;
    private boolean hasLyrics;
    private String lyricsId;
    private String url;
    private String copyright;
    private Album album;
    private Artists artists;
    private List<Image> image;
    private List<DownloadUrl> downloadUrl;
}
