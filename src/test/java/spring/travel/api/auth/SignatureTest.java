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
