package spring.travel.api.controllers;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.travel.api.Application;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class HomeControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9091);

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldReturnOffers() throws Exception {
        stubFor(WireMock.get(urlMatching("/profile/123")).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody("{ \"lifecycle\":\"Family\", \"spending\":\"Economy\", \"gender\":\"male\" }")));
        stubFor(WireMock.get(urlMatching("/loyalty/123")).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody("{ \"group\":\"Bronze\", \"points\":100 }")));
        stubFor(WireMock.get(urlMatching("/offers/123")).
            willReturn(aResponse().
                withHeader("Content-Type", "application/json").
                withBody("[ { \"title\":\"Offer 1\", \"details\":\"Blah blah\", \"image\":\"offer1.jpg\" } ]")));

        MvcResult mvcResult = this.mockMvc.perform(get("/home?id=123").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(status().isOk()).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(isA(List.class))).
            andReturn();


        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().isOk()).
            andExpect(jsonPath("$[0].title").value("Offer 1")).
            andExpect(jsonPath("$[0].image").value("offer1.jpg"));
    }
}
