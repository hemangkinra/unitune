package in.weekend.unitune.controller;


import in.weekend.unitune.configuration.webclient.SpotifyWebClientConfig;
import in.weekend.unitune.dto.requests.ConvertMusicLinkRequest;
import in.weekend.unitune.dto.response.MusicLinkResponse;
import in.weekend.unitune.dto.webclient.SpotifyRestConfigDto;
import in.weekend.unitune.service.music.MusicLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/unitune/v1")
@Slf4j
public class UniTuneConvertController {

    @Autowired
    private MusicLinkService musicLinkService;

    @Autowired
    private SpotifyRestConfigDto spotifyRestConfigDto;

    @PostMapping("/convert")
    public Mono<MusicLinkResponse> convert(@RequestBody ConvertMusicLinkRequest request) {
        return musicLinkService.convertToUniversal(request);
    }

    @GetMapping("/shared/{id}")
    public Mono<MusicLinkResponse> getSavedMusicLink(@PathVariable String id) {
        return musicLinkService.fetchMusicLinkResponse(id);
    }

    @GetMapping("/test")
    public SpotifyRestConfigDto getTest() {
        return spotifyRestConfigDto;
    }
}
