package in.weekend.unitune.service;


import in.weekend.unitune.enums.PlatformType;
import in.weekend.unitune.exceptions.UnsupportedPlatformException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class PlatformDetector {

    private final Map<PlatformType, Pattern> platformPatterns;

    public PlatformDetector() {
        this.platformPatterns = initializePlatformPatterns();
    }

    public PlatformType detectPlatform(String url) throws UnsupportedPlatformException {
        for (Map.Entry<PlatformType, Pattern> entry : platformPatterns.entrySet()) {
            if (entry.getValue().matcher(url).find()) {
                return entry.getKey();
            }
        }
        throw new UnsupportedPlatformException("Unsupported platform for URL: " + url);
    }

    private Map<PlatformType, Pattern> initializePlatformPatterns() {
        Map<PlatformType, Pattern> patterns = new HashMap<>();

        patterns.put(PlatformType.SPOTIFY,
                Pattern.compile("(open\\.)?spotify\\.com/.+"));
        patterns.put(PlatformType.APPLE_MUSIC,
                Pattern.compile("music\\.apple\\.com/.+"));
        patterns.put(PlatformType.YOUTUBE_MUSIC,
                Pattern.compile("music\\.youtube\\.com/.+"));
        patterns.put(PlatformType.JIO_SAAVN,
                Pattern.compile("jiosaavn\\.com/.+"));

        return patterns;
    }
}
