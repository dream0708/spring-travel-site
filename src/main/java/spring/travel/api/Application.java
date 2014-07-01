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
import spring.travel.api.services.LoyaltyService;
import spring.travel.api.services.OffersService;
import spring.travel.api.services.ProfileService;

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
}
