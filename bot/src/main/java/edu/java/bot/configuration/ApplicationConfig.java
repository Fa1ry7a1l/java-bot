package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotEmpty
    String scrapper,

    Retry retry,

    KafkaInfo kafkaInfo
) {
    public record Retry(
        Integer maxAttempts,
        Set<Integer> retryStatusCodes,
        RetryType type,
        ConstantConfig constant,
        LinearConfig linear,
        ExponentialConfig exponential
    ) {
        public record ConstantConfig(
            Long backOffPeriodMillis
        ) {
        }

        public record LinearConfig(
            Long initialIntervalMillis,
            Long maxIntervalMillis
        ) {
        }

        public record ExponentialConfig(
            Long initialIntervalMillis,
            Double multiplier,
            Long maxIntervalMillis
        ) {
        }
    }

    public record KafkaInfo(String servers, Topic topic) {
        public record Topic(
            String name,
            String dlqName,
            Integer partitions,
            Integer replicas
        ) {
        }
    }
}
