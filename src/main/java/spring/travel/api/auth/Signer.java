package spring.travel.api.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Signer {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private String key;

    public Signer(String key) {
        this.key = key;
    }

    public String sign(String data) throws AuthException {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AuthException("Failed signing data", e);
        }
    }

    private String toHex(byte[] data) {
        char[] chars = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            int b = data[i] & 0xff;
            chars[2 * i] = hexChars[b >> 4];
            chars[2 * i + 1] = hexChars[b & 0xf];
        }
        return new String(chars);
    }
}
