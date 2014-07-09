package spring.travel.api.auth;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SignatureTest {

    @Test
    public void signatureShouldBeVerifiable() throws Exception {
        String secretKey = "weuytgwoivowrgirhgo84y3694ytghlw4t8o2hf";
        String data = "98347928472093235";

        Signer signer = new Signer(secretKey);
        String signature = signer.sign(data);

        Verifier verifier = new Verifier(secretKey);
        assertTrue(verifier.verify(data, signature));
    }

    @Test
    public void shouldFailVerifyingAModifiedSignature() throws Exception {
        String secretKey = "weuytgwoivowrgirhgo84y3694ytghlw4t8o2hf";
        String data = "98347928472093235";

        Signer signer = new Signer(secretKey);
        String signature = "4" + signer.sign(data);

        Verifier verifier = new Verifier(secretKey);
        assertFalse(verifier.verify(data, signature));
    }
}
