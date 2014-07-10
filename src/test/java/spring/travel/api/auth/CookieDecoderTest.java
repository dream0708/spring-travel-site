/**
 * Copyright 2014 Andy Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
