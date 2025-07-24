package in.weekend.unitune.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MusicLinkResponse {
    private boolean success;
    private String message;
    private String requestId;
    private MusicLinkData data;
    private List<String> errors;
    private List<String> warnings;
    private long processingTimeMs;

    public MusicLinkResponse() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public MusicLinkResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    // Static factory methods
    public static MusicLinkResponse success(MusicLinkData data) {
        MusicLinkResponse response = new MusicLinkResponse(true, "Successfully converted music link");
        response.setData(data);
        return response;
    }

    public static MusicLinkResponse error(String message) {
        MusicLinkResponse response = new MusicLinkResponse(false, message);
        response.addError(message);
        return response;
    }

    public void addError(String error) {
        this.errors.add(error);
    }


}
