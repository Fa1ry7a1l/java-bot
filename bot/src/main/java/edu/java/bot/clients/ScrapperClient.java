package edu.java.bot.clients;

import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.ApiErrorResponse;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperClient {

    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";
    private static final String TG_CHAT_CONTROLLER_URI = "/tg-chat/{id}";
    private static final String LINK_CONTROLLER_URI = "/links";
    private final WebClient client;

    public ScrapperClient(String baseUrl) {
        client = WebClient.create(baseUrl);
    }

    public boolean registerChat(@NotNull Long tgChatId) {
        client.post()
            .uri(TG_CHAT_CONTROLLER_URI, tgChatId)
            .retrieve()
            .onStatus(
                statusCode -> HttpStatus.CONFLICT.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .handle((apiErrorResponse, sink) -> {
                        sink.error(new ApiErrorResponseException(apiErrorResponse.description()));
                    })
            )
            .toBodilessEntity().block();
        return true;
    }

    public boolean deleteChat(@NotNull Long tgChatId) {
        client.delete()
            .uri(TG_CHAT_CONTROLLER_URI, tgChatId)
            .retrieve()
            .onStatus(
                statusCode -> HttpStatus.NOT_FOUND.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                response -> response.bodyToMono(ApiErrorResponse.class).handle((apiErrorResponse, sink) -> {
                    sink.error(new ApiErrorResponseException(apiErrorResponse.description()));
                })
            )
            .toBodilessEntity().block();
        return true;
    }

    public ListLinksResponse getAllLinksForChat(@NotNull Long tgChatId) {
        return client.get()
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER, tgChatId.toString())
            .retrieve()
            .onStatus(
                statusCode -> HttpStatus.NOT_FOUND.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                response -> response.bodyToMono(ApiErrorResponse.class).handle((apiErrorResponse, sink) -> {
                    sink.error(new ApiErrorResponseException(apiErrorResponse.description()));
                })
            )
            .bodyToMono(ListLinksResponse.class).block();
    }

    public LinkResponse addLink(@NotNull Long tgChatId, @NotNull AddLinkRequest request) {
        return client.post()
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER, tgChatId.toString())
            .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
            .bodyValue(request)
            .retrieve()
            .onStatus(
                statusCode -> HttpStatus.NOT_FOUND.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .handle((apiErrorResponse, sink) -> {
                        sink.error(new ApiErrorResponseException(apiErrorResponse.description()));
                    })
            )
            .bodyToMono(LinkResponse.class).block();
    }

    public LinkResponse removeLink(@NotNull Long tgChatId, @NotNull RemoveLinkRequest request) {
        return client.method(HttpMethod.DELETE)
            .uri(LINK_CONTROLLER_URI)
            .header(TG_CHAT_ID_HEADER, tgChatId.toString())
            .bodyValue(request)
            .retrieve()
            .onStatus(
                statusCode -> HttpStatus.NOT_FOUND.equals(statusCode) || HttpStatus.BAD_REQUEST.equals(statusCode),
                response -> response.bodyToMono(ApiErrorResponse.class).handle((apiErrorResponse, sink) -> {
                    sink.error(new ApiErrorResponseException(apiErrorResponse.description()));
                })
            )
            .bodyToMono(LinkResponse.class).block();
    }

}
