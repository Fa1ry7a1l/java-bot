package edu.java.bot.clients;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.RemoveLinkRequest;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScrapperClientTest {

    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    @RegisterExtension
    private static final WireMockExtension MOCK_SERVER = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().dynamicPort())
        .build();

    @Autowired
    private ScrapperClient client;

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper", MOCK_SERVER::baseUrl);
    }

    @DisplayName("проверка пустого списка ссылок")
    @Test
    public void givenClient_whenRequestEmptyLinkList_thenReceiveOk() {
        MOCK_SERVER.stubFor(WireMock.get("/links").withHeader(TG_CHAT_ID_HEADER, containing("1"))
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "links": [],
                      "size": 0
                    }
                    """)));

        var response = client.getAllLinksForChat(1L).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.links().isEmpty());
        Assertions.assertEquals(0, body.size());
    }

    @DisplayName("проверка непустого списка ссылок")
    @Test
    public void givenClient_whenRequestNonEmptyLinkList_thenReceiveOk() {
        MOCK_SERVER.stubFor(WireMock.get("/links").withHeader(TG_CHAT_ID_HEADER, containing("1"))
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "links": [
                        {
                          "id": 0,
                          "url": "https://vk.com"
                        },
                        {
                          "id": 0,
                          "url": "https://youtube.com"
                        }
                      ],
                      "size": 2
                    }
                    """)));

        var response = client.getAllLinksForChat(1L).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(2, body.links().size());
        Assertions.assertEquals(2, body.size());
        Assertions.assertEquals(URI.create("https://vk.com"), body.links().get(0).url());
        Assertions.assertEquals(URI.create("https://youtube.com"), body.links().get(1).url());
    }

    @DisplayName("проверка непустого списка ссылок")
    @Test
    public void givenClient_whenRequestNonExistingUserLinks_thenReceiveNotFound() {
        MOCK_SERVER.stubFor(WireMock.get("/links").withHeader(TG_CHAT_ID_HEADER, containing("2"))
            .willReturn(WireMock.notFound()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Вы пытаетесь обратиться к пользователю, которого бот не знает",
                      "code": "404 NOT_FOUND",
                      "exceptionName": "UserNotFoundException",
                      "exceptionMessage": "404 NOT_FOUND \\"Пользователь с id 2 не найден\\"",
                      "stacktrace": [
                      ]
                    }
                    """)));

        var exception =
            Assertions.assertThrows(ApiErrorResponseException.class, () -> client.getAllLinksForChat(2L).block());

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getApiErrorResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.toString(), exception.getApiErrorResponse().code());
        Assertions.assertEquals(
            "Вы пытаетесь обратиться к пользователю, которого бот не знает",
            exception.getApiErrorResponse().description()
        );
    }

    @DisplayName("проверка отправки ссылки")
    @Test
    public void givenClient_whenSendLink_thenReceiveOk() {
        URI testURL = URI.create("https://youtube.com");

        MOCK_SERVER.stubFor(WireMock.post("/links")/*.withRequestBody(equalTo("""
                {
                  "link": "https://youtube.com"
                }
                """))*/.withHeader(TG_CHAT_ID_HEADER, containing("1"))
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "id": 0,
                      "url": "https://youtube.com"
                    }
                    """)));

        var response = client.addLink(1L, new AddLinkRequest(testURL)).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(body.url(), testURL);
    }

    @DisplayName("проверка отправки ссылки для несуществующего пользователя")
    @Test
    public void givenClient_whenSendLinkToNonExistingUser_thenReceiveNotFound() {
        URI testURL = URI.create("https://youtube.com");

        MOCK_SERVER.stubFor(WireMock.post("/links").withHeader(TG_CHAT_ID_HEADER, containing("2"))
            .willReturn(WireMock.notFound()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Вы пытаетесь обратиться к пользователю, которого бот не знает",
                      "code": "404 NOT_FOUND",
                      "exceptionName": "UserNotFoundException",
                      "exceptionMessage": "404 NOT_FOUND \\"Пользователь с id 2 не найден\\"",
                      "stacktrace": [
                      ]
                    }
                    """)));

        var exception =
            Assertions.assertThrows(
                ApiErrorResponseException.class,
                () -> client.addLink(2L, new AddLinkRequest(testURL)).block()
            );

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getApiErrorResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.toString(), exception.getApiErrorResponse().code());
        Assertions.assertEquals(
            "Вы пытаетесь обратиться к пользователю, которого бот не знает",
            exception.getApiErrorResponse().description()
        );
    }

    @DisplayName("проверка отправки ссылки для несуществующего пользователя")
    @Test
    public void givenClient_whenDeleteLink_thenReceiveOk() {
        URI testURL = URI.create("https://vk.com");

        MOCK_SERVER.stubFor(WireMock.delete("/links").withHeader(TG_CHAT_ID_HEADER, containing("1"))
            .willReturn(WireMock.ok()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "id": 0,
                      "url": "https://vk.com"
                    }
                    """)));

        var result = client.removeLink(1L, new RemoveLinkRequest(testURL)).block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(testURL, result.getBody().url());
    }

    @DisplayName("проверка удаления несуществующей ссылки")
    @Test
    public void givenClient_whenDeleteNonExistingLink_thenReceiveNotFound() {
        URI testURL = URI.create("https://vk.com");

        MOCK_SERVER.stubFor(WireMock.delete("/links").withHeader(TG_CHAT_ID_HEADER, containing("1"))
            .willReturn(WireMock.notFound()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "попытка удалить ссылку, которой нет",
                      "code": "404 NOT_FOUND",
                      "exceptionName": "DeletingNotExistingUrlException",
                      "exceptionMessage": "404 NOT_FOUND \\"чат 1 пытается удалить ссылку https://vk.com\\"",
                      "stacktrace": [
                      ]
                    }
                    """)));

        var exception =
            Assertions.assertThrows(
                ApiErrorResponseException.class,
                () -> client.removeLink(1L, new RemoveLinkRequest(testURL)).block()
            );

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getApiErrorResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.toString(), exception.getApiErrorResponse().code());
        Assertions.assertEquals(
            "попытка удалить ссылку, которой нет",
            exception.getApiErrorResponse().description()
        );
    }

    @DisplayName("проверка удаления ссылки для несуществующего пользователя")
    @Test
    public void givenClient_whenDeleteNonExistingUserLink_thenReceiveNotFound() {
        URI testURL = URI.create("https://vk.com");

        MOCK_SERVER.stubFor(WireMock.delete("/links").withHeader(TG_CHAT_ID_HEADER, containing("10"))
            .willReturn(WireMock.notFound()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Вы пытаетесь обратиться к пользователю, которого бот не знает",
                      "code": "404 NOT_FOUND",
                      "exceptionName": "UserNotFoundException",
                      "exceptionMessage": "404 NOT_FOUND \\"Пользователь с id 10 не найден\\"",
                      "stacktrace": [
                      ]
                    }
                    """)));

        var exception =
            Assertions.assertThrows(
                ApiErrorResponseException.class,
                () -> client.removeLink(10L, new RemoveLinkRequest(testURL)).block()
            );

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getApiErrorResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.toString(), exception.getApiErrorResponse().code());
        Assertions.assertEquals(
            "Вы пытаетесь обратиться к пользователю, которого бот не знает",
            exception.getApiErrorResponse().description()
        );
    }

    @DisplayName("проверка добавления пользователя")
    @Test
    public void givenClient_whenCreateNewUser_thenReceiveOk() {
        Long id = 1L;

        MOCK_SERVER.stubFor(WireMock.post("/tg-chat/" + id)
            .willReturn(WireMock.ok()));

        var res = client.registerChat(id).block();

        Assertions.assertNotNull(res);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @DisplayName("проверка повторного добавления пользователя")
    @Test
    public void givenClient_whenCreateNewUserAgain_thenReceiveConflict() {
        Long id = 2L;

        MOCK_SERVER.stubFor(WireMock.post("/tg-chat/" + id)
            .willReturn(WireMock.status(409).withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Вы пытаетесь повторно зарегистрироваться в боте",
                      "code": "409 CONFLICT",
                      "exceptionName": "TelegramChatAlreadyRegisteredException",
                      "exceptionMessage": "409 CONFLICT \\"Повторная попытка зарегистрировать чат 2\\"",
                      "stacktrace": [
                      ]
                    }
                    """)));

        var exception =
            Assertions.assertThrows(
                ApiErrorResponseException.class,
                () -> client.registerChat(id).block()
            );

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getApiErrorResponse());
        Assertions.assertEquals(HttpStatus.CONFLICT.toString(), exception.getApiErrorResponse().code());
        Assertions.assertEquals(
            "Вы пытаетесь повторно зарегистрироваться в боте",
            exception.getApiErrorResponse().description()
        );
    }

    @DisplayName("проверка удаления пользователя")
    @Test
    public void givenClient_whenDeleteExistedUser_thenReceiveOk() {
        Long id = 1L;

        MOCK_SERVER.stubFor(WireMock.delete("/tg-chat/" + id)
            .willReturn(WireMock.ok()));

        var res = client.deleteChat(id).block();

        Assertions.assertNotNull(res);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @DisplayName("проверка повторного добавления пользователя")
    @Test
    public void givenClient_whenDeleteNonExistedUser_thenReceiveNotFound() {
        Long id = 2L;

        MOCK_SERVER.stubFor(WireMock.delete("/tg-chat/" + id)
            .willReturn(WireMock.status(404).withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Вы пытаетесь обратиться к пользователю, которого бот не знает",
                      "code": "404 NOT_FOUND",
                      "exceptionName": "UserNotFoundException",
                      "exceptionMessage": "404 NOT_FOUND \\"Пользователь с id 1 не найден\\"",
                      "stacktrace": [
                      ]
                    }
                    """)));

        var exception =
            Assertions.assertThrows(
                ApiErrorResponseException.class,
                () -> client.deleteChat(id).block()
            );

        Assertions.assertNotNull(exception);
        Assertions.assertNotNull(exception.getApiErrorResponse());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.toString(), exception.getApiErrorResponse().code());
        Assertions.assertEquals(
            "Вы пытаетесь обратиться к пользователю, которого бот не знает",
            exception.getApiErrorResponse().description()
        );
    }
}
