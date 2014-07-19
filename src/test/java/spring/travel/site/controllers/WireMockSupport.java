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
package spring.travel.site.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class WireMockSupport {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void stubGet(String url, Object response) throws Exception {
        stubFor(WireMock.get(urlEqualTo(url)).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody(mapper.writeValueAsString(response))));
    }

    public static void stubGet(String url, String response) throws Exception {
        stubFor(WireMock.get(urlEqualTo(url)).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody(response)));
    }

    public static void stubPost(String url, Object body, Object response) throws Exception {
        stubFor(WireMock.post(urlEqualTo(url)).
            withRequestBody(equalToJson(mapper.writeValueAsString(body))).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody(mapper.writeValueAsString(response))));
    }
}
