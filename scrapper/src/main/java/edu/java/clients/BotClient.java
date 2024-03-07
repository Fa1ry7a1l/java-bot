package edu.java.clients;

import edu.java.dtos.ApiErrorResponse;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.exceptions.ApiErrorResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class BotClient {

    private final WebClient client;

    public BotClient(String baseUrl) {
        client = WebClient.create(baseUrl);
    }

    public boolean sendUpdate(LinkUpdateRequest request) {
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
            .toBodilessEntity().block();
        return true;
    }

}
