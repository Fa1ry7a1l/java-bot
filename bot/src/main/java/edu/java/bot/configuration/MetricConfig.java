package edu.java.bot.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Bean
    public Counter processedMessagesCounter(MeterRegistry registry, ApplicationConfig config) {
        return Counter.builder("message_processed")
            .description("count_of_precessed_messages")
            .register(registry);
    }
}
