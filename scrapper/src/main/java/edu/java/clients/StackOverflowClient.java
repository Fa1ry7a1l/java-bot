package edu.java.clients;

import edu.java.clients.dto.StackOverflowDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
public class StackOverflowClient {

    private final WebClient client;
    private final RetryTemplate retryTemplate;

    public StackOverflowClient(String basePath, RetryTemplate retryTemplate) {
        client = WebClient.create(basePath);
        this.retryTemplate = retryTemplate;
    }

    public StackOverflowDTO getQuestionsInfo(String ids) {
        return retryTemplate.execute(context -> client.get()
            .uri(uriBuilder ->
                uriBuilder.path("/questions/{ids}")
                    .queryParam("site", "stackoverflow")
                    .build(ids))
            .retrieve()
            .bodyToMono(StackOverflowDTO.class).block());
    }

}
