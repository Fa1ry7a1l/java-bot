package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(

    Scheduler scheduler,

    Clients clients,

    AccessType databaseAccessType,

    Retry retry,

    KafkaInfo kafkaInfo,

    Boolean useQueue
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record Clients(GitHub gitHub, StackOverflow stackOverflow, Bot bot) {
        public record GitHub(String url) {

        }

        public record StackOverflow(String url) {

        }

        public record Bot(String url) {

        }
    }

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
            Integer partitions,
            Integer replicas
        ) {
        }
    }

}
