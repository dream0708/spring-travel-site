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

import java.nio.charset.StandardCharsets;

public class Verifier {

    private Signer signer;

    public Verifier(String key) {
        signer = new Signer(key);
    }

    public boolean verify(String data, String signature) throws AuthException {
        String calculatedSignature = signer.sign(data);
        return compare(
            calculatedSignature.getBytes(StandardCharsets.UTF_8),
            signature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean compare(byte[] a, byte[] b) {
        if (a.length < b.length) {
            a = pad(a, b);
        } else if (b.length < a.length) {
            b = pad(b, a);
        }

        boolean result = true;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                result = false;
            }
        }
        return result;
    }

    private byte[] pad(byte[] shorter, byte[] longer) {
        byte[] result = new byte[longer.length];
        System.arraycopy(shorter, 0, result, 0, shorter.length);
        for (int i = 0; i < longer.length - shorter.length; i++) {
            result[shorter.length + i] = 0;
        }
        return result;
    }
}
