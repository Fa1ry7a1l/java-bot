package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCommandTest {
    @Mock
    private ScrapperClient client;

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    private Long userId = 1L;

    private ListCommand listCommand;

    @BeforeEach
    public void setUp() {
        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(userId);
        listCommand = new ListCommand(client);

    }

    @Test
    @DisplayName("Получение сообщения /list with exception")
    void givenResponseService_whenReceiveListMessageWithExceptionGettingLinks_thenReturnSpecialMessage() {
        when(client.getAllLinksForChat(any())).thenThrow(new ApiErrorResponseException("Ошибочка"));
        clearInvocations(client);

        var res = listCommand.handle(update);

        Assertions.assertEquals("Ошибочка", res);
    }

    @Test
    @DisplayName("Получение сообщения /list с пустым списком")
    void givenResponseService_whenReceiveListMessageWithEmptyList_thenReturnSpecialMessage() {
        when(client.getAllLinksForChat(any())).thenReturn(new ListLinksResponse(new ArrayList<>(), 0));
        clearInvocations(client);

        var res = listCommand.handle(update);

        Assertions.assertEquals("У вас пока нет ссылок\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /list с непустым списком")
    void givenResponseService_whenReceiveListMessageWithNonEmptyList_thenReturnSpecialMessage() {
        when(client.getAllLinksForChat(any())).thenReturn(new ListLinksResponse(List.of(
            new LinkResponse(
                0L,
                URI.create("https://vk.com")
            ),
            new LinkResponse(0L, URI.create("https://youtube.com"))
        ), 2));
        clearInvocations(client);

        var res = listCommand.handle(update);

        Assertions.assertEquals("Ваши ссылки \n" +
            "1) https://vk.com\n" +
            "2) https://youtube.com\n", res);
    }
}
