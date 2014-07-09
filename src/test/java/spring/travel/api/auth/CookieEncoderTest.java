package spring.travel.api.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookieEncoderTest {

    @Mock
    private Signer mockSigner;

    @Mock
    private Verifier mockVerifier;

    @Test
    public void shouldEncodeAndSignMap() throws Exception {
        CookieEncoder encoder = new CookieEncoder("TEST_SESSION", mockSigner);

        String signature = "9834793875298375295";
        Map<String, String> values = new LinkedHashMap<>();
        values.put("foo", "bar");
        values.put("moose", "antlers");

        when(mockSigner.sign(anyString())).thenReturn(signature);
        String cookie = encoder.encode(values);
        assertEquals("TEST_SESSION=\"" + signature + "-foo=bar&moose=antlers\"", cookie);
    }

    @Test
    public void shouldUrlEncodeKeysAndValues() throws Exception {
        CookieEncoder encoder = new CookieEncoder("MOOSE_SESSION", mockSigner);

        String signature = "9834793875298375295";
        Map<String, String> values = new LinkedHashMap<>();
        values.put("mo ose", "ant@lers");

        when(mockSigner.sign(anyString())).thenReturn(signature);
        String cookie = encoder.encode(values);
        assertEquals("MOOSE_SESSION=\"" + signature + "-mo+ose=ant%40lers\"", cookie);
    }
}
