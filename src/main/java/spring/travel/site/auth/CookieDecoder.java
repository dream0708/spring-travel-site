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
package spring.travel.site.auth;

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
