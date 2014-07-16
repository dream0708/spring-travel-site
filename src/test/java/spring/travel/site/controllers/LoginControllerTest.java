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

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import spring.travel.site.Application;
import spring.travel.site.auth.UserNotFoundException;
import spring.travel.site.auth.Signer;
import spring.travel.site.model.user.Address;
import spring.travel.site.model.LoginData;
import spring.travel.site.model.user.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.travel.site.controllers.WireMockSupport.stubPost;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class LoginControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9101);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private Signer signer;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldReturnBadRequestIfUsernameIsMissing() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            contentType(MediaType.APPLICATION_JSON).
            content("{ \"password\":\"foo\" }").
            accept(MediaType.APPLICATION_JSON)).
            andExpect(status().is(400)).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestIfPasswordIsMissing() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            contentType(MediaType.APPLICATION_JSON).
            content("{ \"username\":\"bill\" }").
            accept(MediaType.APPLICATION_JSON)).
            andExpect(status().is(400)).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestIfBothUsernameAndPasswordAreMissing() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            contentType(MediaType.APPLICATION_JSON).
            accept(MediaType.APPLICATION_JSON)).
            andExpect(status().is(400)).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(400));
    }

    @Test
    public void shouldReturnNotFoundIfUserNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            contentType(MediaType.APPLICATION_JSON).
            content("{ \"username\":\"foo\", \"password\":\"bar\" }").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(instanceOf(UserNotFoundException.class))).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(404));
    }

    @Test
    public void shouldSetSessionCookieIfUserLogsInSuccessfully() throws Exception {
        stubPost("/login",
            new LoginData("brubble", "bambam"),
            new User("346436", "Barney", "Rubble", "brubble", Optional.<Address>empty())
        );

        when(signer.sign("id=346436")).thenReturn("SIGNATURE");

        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            contentType(MediaType.APPLICATION_JSON).
            content("{ \"username\":\"brubble\", \"password\":\"bambam\" }").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(instanceOf(ResponseEntity.class))).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(200));

        Object asyncResult = mvcResult.getAsyncResult(2000);

        assertTrue(asyncResult instanceof ResponseEntity);
        ResponseEntity<User> response = (ResponseEntity<User>)asyncResult;

        User user = response.getBody();
        assertEquals("Barney", user.getFirstName());

        HttpHeaders headers = response.getHeaders();
        List<String> session = headers.get("Set-Cookie");

        assertEquals(1, session.size());
        assertEquals("GETAWAY_SESSION=SIGNATURE-id=346436; path=/", session.get(0));
    }
}
