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
package spring.travel.site;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.mustache.MustacheViewResolver;
import org.springframework.web.servlet.view.mustache.java.MustacheJTemplateFactory;
import spring.travel.site.auth.CookieDecoder;
import spring.travel.site.auth.CookieEncoder;
import spring.travel.site.auth.PlaySessionCookieBaker;
import spring.travel.site.auth.Signer;
import spring.travel.site.auth.Verifier;
import spring.travel.site.controllers.GeoLocator;
import spring.travel.site.request.RequestInfoInterceptor;
import spring.travel.site.request.RequestInfoResolver;
import spring.travel.site.services.AdvertService;
import spring.travel.site.services.LoginService;
import spring.travel.site.services.LoyaltyService;
import spring.travel.site.services.OffersService;
import spring.travel.site.services.ProfileService;
import spring.travel.site.services.UserService;
import spring.travel.site.services.WeatherService;
import spring.travel.site.view.JsonViewResolver;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource("classpath:application.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory());
    }

    @Value("${profile.service.url}")
    private String profileServiceUrl;

    @Bean
    public ProfileService profileService() {
        return new ProfileService(profileServiceUrl);
    }

    @Value("${loyalty.service.url}")
    private String loyaltyServiceUrl;

    @Bean
    public LoyaltyService loyaltyService() {
        return new LoyaltyService(loyaltyServiceUrl);
    }

    @Value("${offers.service.url}")
    private String offersServiceUrl;

    @Bean
    public OffersService offersService() {
        return new OffersService(offersServiceUrl);
    }

    @Value("${login.service.url}")
    private String loginServiceUrl;

    @Bean
    public LoginService loginService() {
        return new LoginService(loginServiceUrl);
    }

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Bean
    public UserService userService() {
        return new UserService(userServiceUrl);
    }

    @Value("${weather.service.url}")
    private String weatherServiceUrl;

    @Bean
    public WeatherService weatherService() {
        return new WeatherService(weatherServiceUrl);
    }

    @Value("${advert.service.url}")
    private String advertServiceUrl;

    @Bean
    public AdvertService advertService() {
        return new AdvertService(advertServiceUrl);
    }

    @Bean
    public GeoLocator geoLocator() {
        return new GeoLocator();
    }

    @Bean
    public ViewResolver jsonViewResolver() {
        return new JsonViewResolver();
    }

    @Bean
    public ViewResolver mustacheViewResolver() {
        MustacheViewResolver resolver = new MustacheViewResolver();
        resolver.setContentType("text/html");
        MustacheJTemplateFactory mustacheJTemplateFactory = new MustacheJTemplateFactory();
        mustacheJTemplateFactory.setResourceLoader(new DefaultResourceLoader());
        resolver.setTemplateFactory(mustacheJTemplateFactory);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".mustache");
        resolver.setCache(false);
        return resolver;
   }

    @Value("${application.secret}")
    private String applicationSecret;

    @Value("${session.cookieName:GETAWAY_SESSION}")
    private String cookieName;

    @Bean
    public Signer signer() {
        return new Signer(applicationSecret);
    }

    @Bean
    public Verifier verifier() {
        return new Verifier(applicationSecret);
    }

    @Bean
    public CookieEncoder cookieEncoder() {
        return new CookieEncoder(cookieName, signer());
    }

    @Bean
    public CookieDecoder cookieDecoder() {
        return new CookieDecoder(verifier());
    }

    @Bean
    public PlaySessionCookieBaker playSessionCookieBaker() {
        return new PlaySessionCookieBaker(cookieEncoder(), cookieDecoder());
    }

    @Value("${request.attributeName:REQUEST-INFO}")
    private String requestAttributeName;

    @Bean
    public AsyncHandlerInterceptor requestInfoInterceptor() {
        return new RequestInfoInterceptor(cookieName, requestAttributeName);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(requestInfoInterceptor());
            }
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                argumentResolvers.add(new RequestInfoResolver());
            }
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                configurer.favorPathExtension(false).
                    useJaf(false).
                    defaultContentType(MediaType.TEXT_HTML).
                    mediaType("json", MediaType.APPLICATION_JSON);
            }
        };
    }
}
