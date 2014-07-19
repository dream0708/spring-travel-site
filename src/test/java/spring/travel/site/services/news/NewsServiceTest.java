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
package spring.travel.site.services.news;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.cache.CacheBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.site.compose.HandOff;
import spring.travel.site.model.NewsItem;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static spring.travel.site.controllers.WireMockSupport.stubGet;

public class NewsServiceTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9101);

    private NewsService newsService;

    @Before
    public void before() {
        newsService = new NewsService("http://localhost:9101/news");
        ReflectionTestUtils.setField(newsService, "asyncRestTemplate", new AsyncRestTemplate());
        ReflectionTestUtils.setField(newsService, "newsCache", CacheBuilder.newBuilder().build());
        ReflectionTestUtils.setField(newsService, "newsDigester", new NewsDigester());
    }

    @Test
    public void shouldReadTheNews() throws Exception {
        stubNewsData("/news", "/grauniad-travel-news.xml");

        HandOff<List<NewsItem>> handOff = new HandOff<>();

        newsService.news().onCompletion(
            items -> handOff.put(items.get())
        ).execute();

        List<NewsItem> newsItems = handOff.get(1);

        assertEquals(2, newsItems.size());

        NewsItem first = newsItems.get(0);

        assertEquals("Londons first board game cafe to open in Hackney", first.getHeadline());

        assertEquals("Draughts, the capitals latest concept cafe, hopes to capitalise on a new trend for beer" +
            " and board games, with over 500 different tabletop games on offer",
            first.getStandFirst());

        assertEquals("http://static.guim.co.uk/sys-images/Guardian/Pix/pictures/" +
            "2014/7/16/1405502998989/e83ad4cc-ec6b-45c7-9270-79b37d905742-460x276.jpeg", first.getImage());

        NewsItem second = newsItems.get(1);

        assertEquals("Travel tips: Oslos trendy new suburb Grünerløkka, and the weeks best deals", second.getHeadline());

        assertEquals("Edgy and urban, this once gritty corner of the Norwegian capital has been given a new lease of" +
            " life. Plus, beach huts in Devon and cheap villas in Ibiza",
            second.getStandFirst());

        assertEquals("http://static.guim.co.uk/sys-images/Guardian/Pix/pictures/" +
            "2014/7/9/1404922550268/09c1c517-bde3-4edc-adf6-0f0deb01b419-460x276.jpeg",
            second.getImage());
    }

    @Test
    public void shouldReturnNewsFromTheCacheIfPresent() throws Exception {
        stubNewsData("/news", "/grauniad-travel-news.xml");

        HandOff<List<NewsItem>> handOff = new HandOff<>(3);

        newsService.news().onCompletion(
            items1 -> {
                handOff.put(items1.get());
                newsService.news().onCompletion(
                    items2 -> handOff.put(items2.get())
                ).execute();
                newsService.news().onCompletion(
                    items3 -> handOff.put(items3.get())
                ).execute();
            }
        ).execute();

        List<List<NewsItem>> items = handOff.getAll(1);

        assertEquals(3, items.size());
        assertEquals(2, items.get(0).size());
        assertEquals(2, items.get(1).size());
        assertEquals(2, items.get(2).size());

        verify(1, getRequestedFor(urlEqualTo("/news")));
    }

    @Test
    public void shouldReturnEmptyListIfNewsDigesterBarfsReadingTheNews() throws Exception {
        stubGet("/news", "");

        HandOff<List<NewsItem>> handOff = new HandOff<>();

        newsService.news().onCompletion(
            items -> handOff.put(items.get())
        ).execute();

        List<NewsItem> newsItems = handOff.get(1);

        assertEquals(0, newsItems.size());
    }

    @Ignore("Needs to return an empty list on failure instead of Optional.empty")
    @Test
    public void shouldReturnEmptyListIfNewsServiceReturns404() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/news")).
            willReturn(aResponse().
                withStatus(404)));

        HandOff<List<NewsItem>> handOff = new HandOff<>();

        newsService.news().onCompletion(
            items -> items.ifPresent(it -> handOff.put(it))
        ).execute();

        List<NewsItem> newsItems = handOff.get(1);

        assertEquals(0, newsItems.size());
    }

    private void stubNewsData(String url, String filename) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(filename);
        String stubData = new Scanner(inputStream).useDelimiter("\\A").next();
        stubGet(url, stubData);
    }
}
