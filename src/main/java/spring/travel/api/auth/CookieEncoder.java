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
