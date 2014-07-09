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
package spring.travel.api.controllers;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.travel.api.TestApplication;
import spring.travel.api.auth.Signer;
import spring.travel.api.auth.Verifier;
import spring.travel.api.model.Gender;
import spring.travel.api.model.Group;
import spring.travel.api.model.LifeCycle;
import spring.travel.api.model.Loyalty;
import spring.travel.api.model.Offer;
import spring.travel.api.model.Profile;
import spring.travel.api.model.Spending;
import spring.travel.api.model.User;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.travel.api.controllers.WireMockSupport.stubGet;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
public class HomeControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9101);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private Signer mockSigner;

    @Autowired
    private Verifier mockVerifier;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldReturnOffersForUser() throws Exception {
        stubGet("/user?id=123", new User("123", "Fred", "Flintstone", "freddyf", null));

        stubGet("/profile/user/123", new Profile(LifeCycle.Family, Spending.Economy, Gender.Male));

        stubGet("/loyalty/user/123", new Loyalty(Group.Bronze, 100));

        List<Offer> offers = Arrays.asList(
            new Offer("Offer 1", "Blah blah", "offer1.jpg"),
            new Offer("Offer 2", "Blah blah", "offer2.jpg"),
            new Offer("Offer 3", "Blah blah", "offer3.jpg")
        );

        stubGet("/offers?lifecycle=family&spending=economy&gender=male&loyalty=bronze", offers);

        String signature = "0923023985092384";
        String cookieName = "GETAWAY_SESSION";
        String encoded = "id=123";
        String cookieValue = signature + "-" + encoded;

        Cookie cookie = Mockito.mock(Cookie.class);
        when(cookie.getName()).thenReturn(cookieName);
        when(cookie.getValue()).thenReturn(cookieValue);

        when(mockVerifier.verify(encoded, signature)).thenReturn(true);

        MvcResult mvcResult = this.mockMvc.perform(get("/").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8")).
            cookie(cookie)).
            andExpect(status().isOk()).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(isA(List.class))).
            andReturn();


        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().isOk()).
            andExpect(jsonPath("$[0].title").value("Offer 1")).
            andExpect(jsonPath("$[0].image").value("offer1.jpg")).
            andExpect(jsonPath("$[1].title").value("Offer 2")).
            andExpect(jsonPath("$[1].image").value("offer2.jpg")).
            andExpect(jsonPath("$[2].title").value("Offer 3")).
            andExpect(jsonPath("$[2].image").value("offer3.jpg"));
    }

    @Test
    public void shouldNotCallProfileAndLoyaltyServicesIfNoUserIsSupplied() throws Exception {
        List<Offer> offers = Arrays.asList(
                new Offer("Offer 1", "Blah blah", "offer1.jpg"),
                new Offer("Offer 2", "Blah blah", "offer2.jpg")
        );

        stubGet("/offers", offers);

        MvcResult mvcResult = this.mockMvc.perform(get("/").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(status().isOk()).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(isA(List.class))).
            andReturn();


        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().isOk()).
            andExpect(jsonPath("$[0].title").value("Offer 1")).
            andExpect(jsonPath("$[0].image").value("offer1.jpg")).
            andExpect(jsonPath("$[1].title").value("Offer 2")).
            andExpect(jsonPath("$[1].image").value("offer2.jpg"));
    }
}
