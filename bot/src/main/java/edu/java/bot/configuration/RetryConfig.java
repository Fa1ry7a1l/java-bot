package edu.java.bot.configuration;

import edu.java.bot.retry.HttpCodeRetryPolicy;
import edu.java.bot.retry.LinearBackOffPolicy;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@AllArgsConstructor
@Configuration
@Log4j2
public class RetryConfig {

    private final ApplicationConfig applicationConfig;

    @Bean
    @ConditionalOnProperty(prefix = "app.retry", name = "type", havingValue = "linear")
    public RetryTemplate retryTemplateLinear() {
        RetryTemplate retryTemplate = new RetryTemplate();

        LinearBackOffPolicy linearBackOffPolicy = new LinearBackOffPolicy(
            applicationConfig.retry().linear().initialIntervalMillis(),
            applicationConfig.retry().linear().maxIntervalMillis()
        );
        retryTemplate.setBackOffPolicy(linearBackOffPolicy);

        retryTemplate.setRetryPolicy(generateRetryPolicy());
        log.info("создан bean политики retry - linear");
        return retryTemplate;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.retry", name = "type", havingValue = "exponential")
    public RetryTemplate retryTemplateExponential() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(applicationConfig.retry().exponential().initialIntervalMillis());
        backOffPolicy.setMultiplier(applicationConfig.retry().exponential().multiplier());
        backOffPolicy.setMaxInterval(applicationConfig.retry().exponential().maxIntervalMillis());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        retryTemplate.setRetryPolicy(generateRetryPolicy());
        log.info("создан bean политики retry - exponential");

        return retryTemplate;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.retry", name = "type", havingValue = "constant")
    public RetryTemplate retryTemplateConstant() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(applicationConfig.retry().constant().backOffPeriodMillis());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        retryTemplate.setRetryPolicy(generateRetryPolicy());
        log.info("создан bean политики retry - constant");

        return retryTemplate;
    }

    private SimpleRetryPolicy generateRetryPolicy() {
        return new HttpCodeRetryPolicy(
            applicationConfig.retry().maxAttempts(),
            applicationConfig.retry().retryStatusCodes()
        );
    }
}
