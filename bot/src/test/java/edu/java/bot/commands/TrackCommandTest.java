package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.LinkChecker;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.LinkResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackCommandTest {
    @Mock
    private ScrapperClient client;

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    private Long userId = 1L;

    private TrackCommand trackCommand;

    @BeforeEach
    public void setUp() {
        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(userId);
        LinkChecker linkChecker = new LinkChecker();
        trackCommand = new TrackCommand(client, linkChecker);

    }

    @Test
    @DisplayName("Получение сообщения /track")
    void givenResponseService_whenReceiveTrackMessage_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track");
        clearInvocations(client);

        var res = trackCommand.handle(update);

        verifyNoInteractions(client);

        Assertions.assertEquals("Введите ссылку с упоминанием протокола. Например /track https://vk.com\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /track vk.com")
    void givenResponseService_whenReceiveTrackMessageWithCorrectLinkWithoutProtocol_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track vk.com");
        clearInvocations(client);

        var res = trackCommand.handle(update);

        verifyNoInteractions(client);

        Assertions.assertEquals("Ссылка должна содержать протокол: http:// или https://\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /track https://vk.com")
    void givenResponseService_whenReceiveTrackMessageWithCorrectLink_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track https://vk.com");
        when(client.addLink(any(), any())).thenReturn(new LinkResponse(0L,URI.create("https://vk.com")));
        clearInvocations(client);

        var res = trackCommand.handle(update);

        verify(client,times(1)).addLink(any(),any());
        verifyNoMoreInteractions(client);

        Assertions.assertEquals("Успешно добавили\n", res);
    }
    @Test
    @DisplayName("Получение сообщения /track https://vk.com с ошибуой добавления")
    void givenResponseService_whenReceiveTrackMessageWithCorrectLinkError_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track https://vk.com");
        when(client.addLink(any(), any())).thenThrow(new ApiErrorResponseException("ошибочка"));
        clearInvocations(client);

        var res = trackCommand.handle(update);

        verify(client,times(1)).addLink(any(),any());
        verifyNoMoreInteractions(client);

        Assertions.assertEquals("ошибочка", res);
    }

}
