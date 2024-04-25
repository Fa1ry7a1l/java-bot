package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@AllArgsConstructor
@Configuration
@EnableCaching
public class ClientConfiguration {
    private final ApplicationConfig applicationConfig;

    private final RetryTemplate retryTemplate;

    @Bean
    public GitHubClient gitHubClient() {
        return new GitHubClient(applicationConfig.clients().gitHub().url(), retryTemplate);
    }

    @Bean
    public StackOverflowClient stackOverflowClient() {
        return new StackOverflowClient(applicationConfig.clients().stackOverflow().url(), retryTemplate);
    }

    @Bean
    public BotClient botClient() {
        return new BotClient(applicationConfig.clients().bot().url(), retryTemplate);
    }

}
