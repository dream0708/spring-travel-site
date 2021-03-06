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
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.Before;
import org.junit.ClassRule;
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
import org.springframework.web.servlet.ModelAndView;
import spring.travel.site.TestApplication;
import spring.travel.site.auth.Signer;
import spring.travel.site.auth.Verifier;
import spring.travel.site.model.Advert;
import spring.travel.site.model.Offer;
import spring.travel.site.model.user.Address;
import spring.travel.site.model.user.Gender;
import spring.travel.site.model.user.Group;
import spring.travel.site.model.user.LifeCycle;
import spring.travel.site.model.user.Loyalty;
import spring.travel.site.model.user.Profile;
import spring.travel.site.model.user.Spending;
import spring.travel.site.model.user.User;
import spring.travel.site.model.weather.DailyForecast;
import spring.travel.site.view.model.AdvertsView;
import spring.travel.site.view.model.DailyForecastView;
import spring.travel.site.view.model.OffersView;

import javax.servlet.http.Cookie;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.travel.site.controllers.WireMockSupport.stubGet;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
public class HomeControllerTest {

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(9101);

    @Rule
    public WireMockClassRule instanceRule = wireMockRule;

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
    public void shouldReturnOffersForUserWithNoAddress() throws Exception {
        stubGet("/user?id=123", new User("123", "Fred", "Flintstone", "freddyf", Optional.<Address>empty()));

        stubWeather("/weather?id=2643741&cnt=5&mode=json");

        stubGet("/profile/user/123", new Profile(LifeCycle.Family, Spending.Economy, Gender.Male));

        stubGet("/loyalty/user/123", new Loyalty(Group.Bronze, 100));

        List<Offer> offers = Arrays.asList(
            new Offer("Offer 1", "Blah blah", "offer1.jpg"),
            new Offer("Offer 2", "Blah blah", "offer2.jpg"),
            new Offer("Offer 3", "Blah blah", "offer3.jpg"),
            new Offer("Offer 4", "Blah blah", "offer4.jpg")
        );

        stubGet("/offers?lifecycle=family&spending=economy&gender=male&loyalty=bronze", offers);

        List<Advert> adverts = Arrays.asList(
            new Advert("Advert 1", "advert1.jpg", "Blah blah"),
            new Advert("Advert 2", "advert2.jpg", "Blah blah"),
            new Advert("Advert 3", "advert3.jpg", "Blah blah"),
            new Advert("Advert 4", "advert4.jpg", "Blah blah"),
            new Advert("Advert 5", "advert5.jpg", "Blah blah")
        );

        stubGet("/adverts?count=5&target=low", adverts);

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
            header("Cookie", cookieName + "=" + cookieValue)).
            andExpect(status().isOk()).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(isA(ModelAndView.class))).
            andReturn();


        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().isOk());

        ModelAndView modelAndView = (ModelAndView)mvcResult.getAsyncResult(1000);

        Map<String,Object> model = modelAndView.getModel();

        User user = (User)model.get("user");
        assertEquals("Fred", user.getFirstName());

        OffersView modelOffers = (OffersView)model.get("offers");
        assertNotNull(modelOffers);
        assertEquals("Offer 1", modelOffers.getOffers().get(0).getTitle());

        AdvertsView modelAdverts = (AdvertsView)model.get("adverts");
        assertNotNull(modelAdverts);
        assertEquals("Advert 1", modelAdverts.getAdverts().get(0).getTitle());

        DailyForecastView modelForecast = (DailyForecastView)model.get("weather");
        assertEquals("Colnbrook", modelForecast.getCity());
    }

    @Test
    public void shouldNotCallProfileAndLoyaltyServicesIfNoUserIsSupplied() throws Exception {
        stubWeather("/weather?id=2643741&cnt=5&mode=json");

        List<Offer> offers = Arrays.asList(
            new Offer("Offer 1", "Blah blah", "offer1.jpg"),
            new Offer("Offer 2", "Blah blah", "offer2.jpg"),
            new Offer("Offer 3", "Blah blah", "offer3.jpg"),
            new Offer("Offer 4", "Blah blah", "offer4.jpg")
        );

        stubGet("/offers", offers);

        List<Advert> adverts = Arrays.asList(
            new Advert("Advert 1", "advert1.jpg", "Blah blah"),
            new Advert("Advert 2", "advert2.jpg", "Blah blah"),
            new Advert("Advert 3", "advert3.jpg", "Blah blah"),
            new Advert("Advert 4", "advert4.jpg", "Blah blah"),
            new Advert("Advert 5", "advert5.jpg", "Blah blah")
        );

        stubGet("/adverts?count=5", adverts);

        MvcResult mvcResult = this.mockMvc.perform(get("/").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(status().isOk()).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(isA(ModelAndView.class))).
            andReturn();


        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().isOk());

        ModelAndView modelAndView = (ModelAndView)mvcResult.getAsyncResult(1000);

        Map<String,Object> model = modelAndView.getModel();

        assertNull(model.get("user"));

        OffersView modelOffers = (OffersView)model.get("offers");
        assertNotNull(modelOffers);
        assertEquals("Offer 1", modelOffers.getOffers().get(0).getTitle());

        AdvertsView modelAdverts = (AdvertsView)model.get("adverts");
        assertNotNull(modelAdverts);
        assertEquals("Advert 1", modelAdverts.getAdverts().get(0).getTitle());

        DailyForecastView modelForecast = (DailyForecastView)model.get("weather");
        assertEquals("Colnbrook", modelForecast.getCity());
    }

    private void stubWeather(String url) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/weather-lhr-3days.json");
        ObjectMapper mapper = new ObjectMapper();
        DailyForecast stubData = mapper.readValue(inputStream, DailyForecast.class);
        inputStream.close();
        stubGet(url, stubData);
    }
}
