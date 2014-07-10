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
package spring.travel.api.services;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.api.compose.HandOff;
import spring.travel.api.model.Address;
import spring.travel.api.model.User;
import spring.travel.api.model.weather.Location;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static spring.travel.api.controllers.WireMockSupport.stubGet;

public class UserServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9101);

    private UserService userService;

    @Before
    public void before() {
        userService = new UserService("http://localhost:9101/user");
        ReflectionTestUtils.setField(userService, "asyncRestTemplate", new AsyncRestTemplate());
    }

    @Test
    public void shouldReturnUserWithNoAddress() throws Exception {
        stubGet("/user?id=123", new User("123", "Fred", "Flintstone", "freddyf", Optional.<Address>empty()));

        HandOff<Optional<User>> handOff = new HandOff<>();

        userService.user(Optional.of("123")).onCompletion(
            user -> handOff.put(user)
        ).execute();

        Optional<User> optionalUser = handOff.get(1);
        assertNotEquals(Optional.empty(), optionalUser);

        User user = optionalUser.get();
        assertEquals("Fred", user.getFirstName());
        assertEquals(Optional.empty(), user.getAddress());
    }

    @Test
    public void shouldReturnUserWithAnAddress() throws Exception {
        stubGet("/user?id=123", new User("123", "Fred", "Flintstone", "freddyf",
            Optional.of(new Address("345 Stonecave Road", "Bedrock", "70777", "US",
                new Location(5290307, 36.128551, -112.036070)))));

        HandOff<Optional<User>> handOff = new HandOff<>();

        userService.user(Optional.of("123")).onCompletion(
            user -> handOff.put(user)
        ).execute();

        Optional<User> optionalUser = handOff.get(1);
        assertNotEquals(Optional.empty(), optionalUser);

        User user = optionalUser.get();
        assertEquals("Fred", user.getFirstName());
        assertNotEquals(Optional.empty(), user.getAddress());

        Address address = user.getAddress().get();
        assertEquals("345 Stonecave Road", address.getFirstLine());
        assertEquals(5290307, address.getLocation().getCityId());
    }
}
