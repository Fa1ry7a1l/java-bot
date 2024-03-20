package edu.java.clients;

import edu.java.clients.dto.StackOverflowDTO;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClient {

    private final WebClient client;

    public StackOverflowClient(String basePath) {
        client = WebClient.create(basePath);
    }

    public StackOverflowDTO getQuestionsInfo(String ids) {
        return client.get()
            .uri(uriBuilder ->
                uriBuilder.path("/questions/{ids}")
                    .queryParam("site", "stackoverflow")
                    .build(ids))
            .retrieve()
            .bodyToMono(StackOverflowDTO.class).block();
    }

}
