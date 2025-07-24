package in.weekend.unitune.libs;


import in.weekend.unitune.exceptions.MalformedUrlException;
import java.net.URI;
import java.net.URISyntaxException;

public class StringUtils extends org.apache.commons.lang3.StringUtils {


    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public static String encodeToBase62(Long num) {
        if (num == 0) return "0";

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % BASE);
            sb.append(BASE62_ALPHABET.charAt(remainder));
            num /= BASE;
        }

        return sb.reverse().toString();
    }

    public static Long decodeFromBase62(String base62) {
        long result = 0;
        for (int i = 0; i < base62.length(); i++) {
            result = result * BASE + BASE62_ALPHABET.indexOf(base62.charAt(i));
        }
        return result;
    }

    public static String lowerFirstChar(String input) {
        if (isEmpty(input))
            return input;
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }

    public static void handleMalformedUrls(String url) {
        if (isEmpty(url)) {
            throw new MalformedUrlException("Url can not be empty");
        }
        try {
            new URI(url);
        } catch (Exception e) {
            throw new MalformedUrlException("Not a valid URL");
        }
    }

    public static String normalizeUrl(String inputUrl) {
        try {
            // Add https if missing protocol
            if (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://")) {
                inputUrl = "https://" + inputUrl;
            }

            URI uri = new URI(inputUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            String path = extractUrlPath(uri, host);

            // Reconstruct URL properly
            StringBuilder normalizedUrl = new StringBuilder();
            normalizedUrl.append(scheme).append("://").append(host);

            // Add port if it's not the default port for the scheme
            if (port != -1 &&
                    !((scheme.equals("http") && port == 80) ||
                            (scheme.equals("https") && port == 443))) {
                normalizedUrl.append(":").append(port);
            }

            normalizedUrl.append(path);

            return normalizedUrl.toString();

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + inputUrl, e);
        }
    }

    private static String extractUrlPath(URI uri, String host) {
        String path = uri.getPath();

        // Validate essential components
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Invalid host in URL");
        }

        // Normalize path - remove trailing slashes
        if (path != null && !path.isEmpty()) {
            path = path.replaceAll("/+$", "");
            // If path becomes empty after removing slashes, don't add it
            if (path.isEmpty()) {
                path = "";
            }
        } else {
            path = ""; // No path at all
        }
        return path;
    }
}
