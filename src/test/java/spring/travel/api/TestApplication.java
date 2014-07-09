package spring.travel.api;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import spring.travel.api.auth.Signer;
import spring.travel.api.auth.Verifier;

@Configuration
@Import(Application.class)
public class TestApplication {

    @Bean
    public Signer signer() {
        return Mockito.mock(Signer.class);
    }

    @Bean
    public Verifier verifier() {
        return Mockito.mock(Verifier.class);
    }
}
