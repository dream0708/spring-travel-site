package spring.travel.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.AsyncRestTemplate;

public class WeatherService {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private String url;

    public WeatherService(String url) {
        this.url = url;
    }
}
