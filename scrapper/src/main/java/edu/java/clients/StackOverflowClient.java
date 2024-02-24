package edu.java.clients;

import edu.java.clients.dto.StackOverflowDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowClient {


    private final WebClient client;

    public StackOverflowClient(String basePath) {
        client = WebClient.create(basePath);
    }

    public Mono<StackOverflowDTO> getQuestionsInfo(String ids) {
        return client.get()
            .uri("/questions/" + ids + "?site=stackoverflow")
            .retrieve()
            .bodyToMono(StackOverflowDTO.class);
    }

}
