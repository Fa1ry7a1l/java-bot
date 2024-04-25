package edu.java.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.clients.dto.GitHubDTO;
import edu.java.scrapper.IntegrationTest;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitHubClientTest extends IntegrationTest {

    @RegisterExtension
    private static final WireMockExtension MOCK_SERVER = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().dynamicPort())
        .build();

    @Autowired
    private GitHubClient gitHubClient;

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.clients.github.url", MOCK_SERVER::baseUrl);
    }

    @DisplayName("проверка доступного репозитория")
    @Test
    public void givenClient_whenRequestAccessibleRepository_thenReceiveTime() {
        final String repositoryPath = "Fa1ry7a1l/java-bot";
        final OffsetDateTime repositoryUpdatedAt = OffsetDateTime.of(2024, 2, 23, 13, 03, 07, 0, ZoneOffset.of("Z"));

        MOCK_SERVER.stubFor(WireMock.get("/repos/" + repositoryPath)
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "updated_at": "2024-02-23T13:03:07Z"
                    }
                    """)));

        GitHubDTO response = gitHubClient.getQuestionsInfo(repositoryPath);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(repositoryUpdatedAt, response.lastActivityDate());
    }

    @DisplayName("Ошибка при попытке доступа к приватному репозиторию")
    @Test
    public void givenClient_whenRequestNonAccessibleRepository_thenReceiveException() {
        final String repositoryPath = "nouser/norepo";

        MOCK_SERVER.stubFor(WireMock.get("/repos/" + repositoryPath)
            .willReturn(WireMock.notFound()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "message": "Not Found",
                        "documentation_url": "https://docs.github.com/rest/repos/repos#get-a-repository"
                    }
                    """)));

        assertThrows(WebClientResponseException.class, () -> gitHubClient.getQuestionsInfo(repositoryPath));
    }

}
