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
package spring.travel.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import spring.travel.api.auth.CookieDecoder;
import spring.travel.api.auth.CookieEncoder;
import spring.travel.api.auth.OptionalSessionInterceptor;
import spring.travel.api.auth.OptionalSessionResolver;
import spring.travel.api.auth.PlaySessionCookieBaker;
import spring.travel.api.auth.Signer;
import spring.travel.api.auth.Verifier;
import spring.travel.api.services.LoginService;
import spring.travel.api.services.LoyaltyService;
import spring.travel.api.services.OffersService;
import spring.travel.api.services.ProfileService;
import spring.travel.api.services.UserService;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource("classpath:application.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${profile.service.url}")
    private String profileServiceUrl;

    @Value("${loyalty.service.url}")
    private String loyaltyServiceUrl;

    @Value("${offers.service.url}")
    private String offersServiceUrl;

    @Value("${login.service.url}")
    private String loginServiceUrl;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate();
    }

    @Bean
    public ProfileService profileService() {
        return new ProfileService(profileServiceUrl);
    }

    @Bean
    public LoyaltyService loyaltyService() {
        return new LoyaltyService(loyaltyServiceUrl);
    }

    @Bean
    public OffersService offersService() {
        return new OffersService(offersServiceUrl);
    }

    @Bean
    public LoginService loginService() {
        return new LoginService(loginServiceUrl);
    }

    @Bean
    public UserService userService() {
        return new UserService(userServiceUrl);
    }

    @Value("${application.secret}")
    private String applicationSecret;

    @Value("${session.cookieName}")
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

    @Bean
    public AsyncHandlerInterceptor optionalUserInterceptor() {
        return new OptionalSessionInterceptor(cookieName);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(optionalUserInterceptor());
            }
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
                argumentResolvers.add(new OptionalSessionResolver());
            }
        };
    }
}
