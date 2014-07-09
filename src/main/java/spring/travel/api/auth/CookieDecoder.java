package spring.travel.api.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieDecoder {

    private final Verifier verifier;

    public CookieDecoder(Verifier verifier) {
        this.verifier = verifier;
    }

    public Map<String, String> decode(String cookie) throws AuthException {
        String[] parts = cookie.split("-", 2);
        if (parts.length != 2) {
            return Collections.emptyMap();
        }

        String signature = parts[0];
        String encoded = parts[1];

        if (!verifier.verify(encoded, signature)) {
            return Collections.emptyMap();
        }

        return Arrays.asList(encoded.split("&")).stream().map(
            keyValue -> keyValue.split("=")
        ).collect(
            Collectors.toMap(
                arr -> urlDecode(arr[0]),
                arr -> arr.length > 1 ? urlDecode(arr[1]) : ""
            )
        );
    }

    private String urlDecode(String arg) {
        try {
            return URLDecoder.decode(arg, "UTF-8");
        } catch (UnsupportedEncodingException bogus) {
            return arg;
        }
    }
}
