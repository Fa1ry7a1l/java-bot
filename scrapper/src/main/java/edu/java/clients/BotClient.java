package edu.java.clients;

import edu.java.dtos.ApiErrorResponse;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.exceptions.ApiErrorResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {

    private final WebClient client;

    public BotClient(String baseUrl) {
        client = WebClient.create(baseUrl);
    }

    public Mono<ResponseEntity<Void>> sendUpdate(LinkUpdateRequest request) {
        return client.post()
            .uri("/updates")
            .bodyValue(request)
            .retrieve()
            .onStatus(
                statusCode -> HttpStatus.CONFLICT.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                response -> response.bodyToMono(ApiErrorResponse.class).map(ApiErrorResponseException::new)
            )
            .toBodilessEntity();
    }

}
