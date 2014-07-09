package spring.travel.api.auth;

import java.util.Map;

public class PlaySessionCookieBaker implements SessionCookieBaker {

    private final CookieEncoder encoder;
    private final CookieDecoder decoder;

    public PlaySessionCookieBaker(CookieEncoder encoder, CookieDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public String encode(Map<String, String> values) throws AuthException {
        return encoder.encode(values);
    }

    public Map<String, String> decode(String cookie) throws AuthException {
        return decoder.decode(cookie);
    }
}
