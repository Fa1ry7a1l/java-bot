package edu.java.bot.retry;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.stereotype.Service;

public class LinearBackOffPolicy implements BackOffPolicy {
    private static String intervalSizeErrorText = "Интервал не может быть отрицательным";

    private final Long initialInterval;
    private final Long maxInterval;

    public LinearBackOffPolicy(Long initialInterval, Long maxInterval) {
        if (maxInterval > 0) {
            this.maxInterval = maxInterval;
        } else {
            throw new IllegalArgumentException(intervalSizeErrorText);
        }
        if (initialInterval > 0) {
            this.initialInterval = initialInterval;
        } else {
            throw new IllegalArgumentException(intervalSizeErrorText);
        }
    }

    @Override
    public BackOffContext start(RetryContext context) {
        return new LinearBackOffContext();
    }

    @SneakyThrows
    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        LinearBackOffContext linearBackOffContext = (LinearBackOffContext) backOffContext;

        Thread.sleep(Math.min(initialInterval * ++linearBackOffContext.attempt, maxInterval));
    }

    @Getter
    @Service
    private class LinearBackOffContext implements BackOffContext {
        private Integer attempt = 0;
    }
}
