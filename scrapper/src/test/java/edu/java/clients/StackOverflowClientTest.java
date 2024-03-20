package edu.java.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.clients.dto.StackOverflowDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import edu.java.configuration.ApplicationConfig;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StackOverflowClientTest extends IntegrationTest {
    @RegisterExtension
    private static final WireMockExtension WIRE_MOCK_SERVER = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().dynamicPort())
        .build();

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.clients.stackOverflow.url", WIRE_MOCK_SERVER::baseUrl);
    }

    @Autowired
    StackOverflowClient stackOverflowClient;

    @Autowired ApplicationConfig config;

    @Test
    public void test()
    {
        System.out.println(config.clients().gitHub().url());
        System.out.println(config.clients().stackOverflow().url());
    }
    @DisplayName("существующий вопрос")
    @Test
    public void givenClient_whenRequestExistingQuestion_thenReceiveTime() {
        final String ids = "78003091";
        int epochSecond = 1708788672;
        final OffsetDateTime lastActivityDate =
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("Z"));

        WIRE_MOCK_SERVER.stubFor(WireMock.get("/questions/" + ids + "?site=stackoverflow")
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "items": [
                            {
                                "last_activity_date": %d
                            }
                        ]
                    }
                    """.formatted(epochSecond))));

        StackOverflowDTO response = stackOverflowClient.getQuestionsInfo(ids);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.items().isEmpty());
        Assertions.assertEquals(lastActivityDate, response.items().getFirst().lastActivityDate());
    }

    @Test
    public void givenClient_whenRequestNonExistingQuestion_thenReceiveEmptyItems() {
        final String ids = "7777777777777777777";

        WIRE_MOCK_SERVER.stubFor(WireMock.get("/questions/" + ids + "?site=stackoverflow")
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                        "items": []
                    }
                    """)));

        StackOverflowDTO response = stackOverflowClient.getQuestionsInfo(ids);

        Assertions.assertNotNull(response);
        assertTrue(response.items().isEmpty());
    }

}
