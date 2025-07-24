package in.weekend.unitune.dto.webclient;


import lombok.Data;

@Data
public class MusicPlatformRestConfigDto {
    String baseUrl;
    String searchMusicEndpoint;
    String fetchMusicDetailEndpoint;
}
