package spring.travel.api.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CookieDecoderTest {

    @Mock
    private Verifier mockVerifier;

    @Test
    public void shouldDecodeCookie() throws Exception {
        CookieDecoder decoder = new CookieDecoder(mockVerifier);

        String signature = "0u4t39jf038uy38tg409jf3r";
        String valueString = "foo=bar&moose=antlers";

        when(mockVerifier.verify(valueString, signature)).thenReturn(true);

        String cookie = signature + "-" + valueString;
        Map<String,String> values = decoder.decode(cookie);

        assertEquals("bar", values.get("foo"));
        assertEquals("antlers", values.get("moose"));
    }

    @Test
    public void shouldReturnEmptyMapIfCookieStringContainsNoSeparator() throws Exception {
        CookieDecoder decoder = new CookieDecoder(mockVerifier);
        Map<String, String> values = decoder.decode("0urt09u0t9u049gu0e=rijge0");
        assertTrue(values.isEmpty());
    }

    @Test
    public void shouldReturnEmptyMapIfCookieStringContainsMoreThanOneSeparator() throws Exception {
        CookieDecoder decoder = new CookieDecoder(mockVerifier);
        Map<String, String> values = decoder.decode("0urt09u0t9u-049gu0e-rijge0");
        assertTrue(values.isEmpty());
    }

    @Test
    public void shouldReturnEmptyMapIfSignatureDoesntVerify() throws Exception {
        CookieDecoder decoder = new CookieDecoder(mockVerifier);

        String signature = "0u4t39jf038uy38tg409jf3r";
        String valueString = "foo=bar&moose=antlers";

        when(mockVerifier.verify(valueString, signature)).thenReturn(false);

        String cookie = signature + "-" + valueString;
        Map<String,String> values = decoder.decode(cookie);
        assertTrue(values.isEmpty());
    }

    @Test
    public void shouldUrlDecodeKeysAndValues() throws Exception {
        CookieDecoder decoder = new CookieDecoder(mockVerifier);

        String signature = "0u4t39jf038uy38tg409jf3r";
        String valueString = "mo+ose=ant%40lers";

        when(mockVerifier.verify(valueString, signature)).thenReturn(true);

        String cookie = signature + "-" + valueString;
        Map<String,String> values = decoder.decode(cookie);

        assertEquals("ant@lers", values.get("mo ose"));
    }

    @Test
    public void shouldIncludeEmptyValues() throws Exception {
        CookieDecoder decoder = new CookieDecoder(mockVerifier);

        String signature = "0u4t39jf038uy38tg409jf3r";
        String valueString = "foo=&moose=antlers";

        when(mockVerifier.verify(valueString, signature)).thenReturn(true);

        String cookie = signature + "-" + valueString;
        Map<String,String> values = decoder.decode(cookie);

        assertEquals("", values.get("foo"));
        assertEquals("antlers", values.get("moose"));
    }
}
