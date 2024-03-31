package edu.java.bot.retry;

import java.util.Set;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.HttpClientErrorException;

public class HttpCodeRetryPolicy extends SimpleRetryPolicy {

    private final Set<Integer> statusCodes;

    public HttpCodeRetryPolicy(Integer maxAttempts, Set<Integer> statusCodes) {
        super(maxAttempts);

        this.statusCodes = statusCodes;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        if (context.getLastThrowable() instanceof HttpClientErrorException e) {
            return statusCodes.contains(e.getStatusCode().value());
        }
        return super.canRetry(context);
    }
}
