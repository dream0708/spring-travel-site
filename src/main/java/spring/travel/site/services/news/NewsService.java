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

import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.AsyncRestTemplate;
import spring.travel.site.compose.AsyncTask;
import spring.travel.site.compose.ImmediatelySomethingAsyncTaskAdapter;
import spring.travel.site.compose.ListenableFutureAsyncTaskAdapter;
import spring.travel.site.model.NewsItem;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class NewsService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    private Cache<String, List<NewsItem>> newsCache;

    @Autowired
    private NewsDigester newsDigester;

    private String url;

    public NewsService(String url) {
        this.url = url;
    }

    public AsyncTask<List<NewsItem>> news() {
        List<NewsItem> newsItems = newsCache.getIfPresent(url);
        if (newsItems != null) {
            return new ImmediatelySomethingAsyncTaskAdapter<>(newsItems);
        }

        return new ListenableFutureAsyncTaskAdapter<List<NewsItem>>(
            () -> asyncRestTemplate.execute(
                URI.create(url),
                HttpMethod.GET,
                (request) -> request.getHeaders().add("Accept", MediaType.APPLICATION_XML_VALUE),
                (response) -> {
                    try {
                        return new ResponseEntity<>(newsDigester.from(response.getBody()), HttpStatus.OK);
                    } catch (IOException ioe) {
                        return new ResponseEntity<>(Collections.<NewsItem>emptyList(), HttpStatus.NO_CONTENT);
                    }
                }
            ),
            (optionalNewsItems) -> optionalNewsItems.ifPresent(items -> newsCache.put(url, items))
        );
    }
}
