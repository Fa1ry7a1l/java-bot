package edu.java.clients;

import edu.java.dtos.ApiErrorResponse;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.exceptions.ApiErrorResponseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
public class BotClient {

    private final WebClient client;

    private final RetryTemplate retryTemplate;

    public BotClient(String baseUrl, RetryTemplate retryTemplate) {
        client = WebClient.create(baseUrl);
        this.retryTemplate = retryTemplate;
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate retryTemplate1 = new RetryTemplate();

        return retryTemplate1;
    }

    public boolean sendUpdate(LinkUpdateRequest request) {

        retryTemplate.execute(context ->
            client.post()
                .uri("/updates")
                .bodyValue(request)
                .retrieve()
                .onStatus(
                    statusCode -> HttpStatus.CONFLICT.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                    response -> response.bodyToMono(ApiErrorResponse.class).handle((apiErrorResponse, sink) -> {
                        sink.error(new ApiErrorResponseException(apiErrorResponse.description()));
                    })
                )
                .toBodilessEntity().block());

        return true;
    }

}
