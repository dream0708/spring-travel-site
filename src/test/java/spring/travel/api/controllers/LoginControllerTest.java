package spring.travel.api.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import spring.travel.api.Application;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class LoginControllerTest {

//    @Rule
//    public WireMockRule wireMockRule = new WireMockRule(9101);

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldReturnBadRequestIfUsernameIsMissing() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            param("password", "blah").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(status().is(400)).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestIfPasswordIsMissing() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            param("username", "blah").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(status().is(400)).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(400));
    }

    @Test
    public void shouldReturnBadRequestIfBothUsernameAndPasswordAreMissing() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(status().is(400)).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(400));
    }

    @Test
    public void shouldReturnNotFoundIfUserNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login").
            param("username", "foo").
            param("password", "bar").
            accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
            andExpect(request().asyncStarted()).
            andExpect(request().asyncResult(instanceOf(NotFoundException.class))).
            andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult)).
            andExpect(status().is(404));
    }
}
