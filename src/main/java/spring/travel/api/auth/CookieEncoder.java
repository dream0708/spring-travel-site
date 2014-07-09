package spring.travel.api.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieEncoder {

    private String cookieName;
    private Signer signer;

    public CookieEncoder(String cookieName, Signer signer) {
        this.cookieName = cookieName;
        this.signer = signer;
    }

    public String encode(Map<String, String> values) throws AuthException {
        String encoded = values.entrySet().stream().map(
            (entry) -> urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue())
        ).collect(Collectors.joining("&"));

        String signature = signer.sign(encoded);
        return cookieName + "=" + signature + "-" + encoded;
    }

    private String urlEncode(String arg) {
        try {
            return URLEncoder.encode(arg, "UTF-8");
        } catch (UnsupportedEncodingException bogus) {
            return arg;
        }
    }
}
