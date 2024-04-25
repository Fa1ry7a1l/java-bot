package edu.java.clients;

import edu.java.clients.dto.GitHubDTO;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient {

    private final WebClient client;
    private final RetryTemplate retryTemplate;

    public GitHubClient(String basePath, RetryTemplate retryTemplate) {
        client = WebClient.create(basePath);
        this.retryTemplate = retryTemplate;
    }

    public GitHubDTO getQuestionsInfo(String repository) {
        return retryTemplate.execute(context ->
            client.get()
                .uri("/repos/" + repository)
                .retrieve()
                .bodyToMono(GitHubDTO.class).block());
    }

}
