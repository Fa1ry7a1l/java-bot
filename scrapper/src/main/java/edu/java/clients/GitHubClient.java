package edu.java.clients;

import edu.java.clients.dto.GitHubDTO;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient {

    private final WebClient client;

    public GitHubClient(String basePath) {
        client = WebClient.create(basePath);
    }

    public GitHubDTO getQuestionsInfo(String repository) {
        return client.get()
            .uri("/repos/" + repository)
            .retrieve()
            .bodyToMono(GitHubDTO.class).block();
    }

}
