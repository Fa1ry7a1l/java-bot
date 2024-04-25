package edu.java.bot.configuration;

import edu.java.bot.clients.ScrapperClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@AllArgsConstructor
public class ScrapperConfig {
    private final ApplicationConfig config;
    private final RetryTemplate retryTemplate;

    @Bean
    public ScrapperClient scrapperClient() {
        return new ScrapperClient(config.scrapper(), retryTemplate);
    }
}
