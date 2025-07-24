package in.weekend.unitune.enums;


import lombok.Getter;

@Getter
public enum PlatformType {
    SPOTIFY("Spotify", "spotify.com"),
    APPLE_MUSIC("Apple Music", "music.apple.com"),
    YOUTUBE_MUSIC("YouTube Music", "music.youtube.com"),
    JIO_SAAVN("JioSaavn", "jiosaavn.com");

    private final String displayName;
    private final String domain;

    PlatformType(String displayName, String domain) {
        this.displayName = displayName;
        this.domain = domain;
    }
}
