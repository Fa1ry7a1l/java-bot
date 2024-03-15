package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
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
class StartCommandTest {
    @Mock
    private ScrapperClient client;

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    private Long userId = 1L;

    private StartCommand startCommand;

    @BeforeEach
    public void setUp() {
        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(userId);
        startCommand = new StartCommand(client);

    }

    @Test
    @DisplayName("Получение сообщения /start возвращает ошибку")
    void givenResponseService_whenReceiveStartWithError_thenReturnSpecialMessage() {
        when(client.registerChat(any())).thenThrow(new ApiErrorResponseException("Ошибочка"));
        clearInvocations(client);

        var res = startCommand.handle(update);

        Assertions.assertEquals("Ошибочка", res);
    }

    @Test
    @DisplayName("Получение сообщения /start")
    void givenResponseService_whenReceiveStart_thenReturnSpecialMessage() {
        when(client.registerChat(any())).thenReturn(true);
        clearInvocations(client);

        var res = startCommand.handle(update);

        Assertions.assertEquals("Я вас запомнил\n", res);
    }
}
