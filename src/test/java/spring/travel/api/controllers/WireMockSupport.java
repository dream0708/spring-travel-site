package spring.travel.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
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
}
