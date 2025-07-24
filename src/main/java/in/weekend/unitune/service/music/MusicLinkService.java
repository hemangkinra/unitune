package in.weekend.unitune.service.music;

import in.weekend.unitune.dto.requests.ConvertMusicLinkRequest;
import in.weekend.unitune.dto.response.MusicLinkResponse;
import reactor.core.publisher.Mono;

public interface MusicLinkService {
    Mono<MusicLinkResponse> convertToUniversal(ConvertMusicLinkRequest request);
    Mono<MusicLinkResponse> fetchMusicLinkResponse(String uniTuneId);
}