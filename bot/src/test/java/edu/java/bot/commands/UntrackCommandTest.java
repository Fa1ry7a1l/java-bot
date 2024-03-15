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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UntrackCommandTest {

    @Mock
    private ScrapperClient client;

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    private Long userId = 1L;

    private UntrackCommand untrackCommand;

    @BeforeEach
    public void setUp() {
        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(userId);

        untrackCommand = new UntrackCommand(client);

    }

    @Test
    @DisplayName("Получение сообщения /untrack")
    void givenResponseService_whenReceiveUntrackMessage_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack");
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verifyNoInteractions(client);

        Assertions.assertEquals("Добавьте номер ссылки. Например /untrack 1\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /untrack 1f23gh")
    void givenResponseService_whenReceiveUntrackMessageWithIncorrectNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 1f23gh");
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verifyNoInteractions(client);
        Assertions.assertEquals("Не удалось распознать номер ссылки. Команда должна выглядить: /untrack 1\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /untrack 1 и ошибки получения всех ссылок с сервера")
    void givenResponseService_whenReceiveUntrackMessageWithCorrectNumberAndGetAllLinksException_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 1");
        when(client.getAllLinksForChat(userId)).thenThrow(new ApiErrorResponseException("А почему бы и не кинуть"));
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verify(client, times(1)).getAllLinksForChat(userId);

        Assertions.assertEquals(
            "Вы ввели число 1. У вас нет ссылки под таким номером. Полный список ссылок с номерами можно посмотреть при помощи команды /list\n",
            res
        );
    }

    @Test
    @DisplayName("Получение сообщения /untrack 1 и получения пустого списка ссылок")
    void givenResponseService_whenReceiveUntrackMessageWithCorrectNumberAndGetAllLinksVoid_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 1");
        when(client.getAllLinksForChat(userId)).thenReturn(new ListLinksResponse(new ArrayList<>(), 0));
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verify(client, times(1)).getAllLinksForChat(userId);

        Assertions.assertEquals(
            "Вы ввели число 1. У вас нет ссылки под таким номером. Полный список ссылок с номерами можно посмотреть при помощи команды /list\n",
            res
        );
    }

    @Test
    @DisplayName("Получение сообщения /untrack 0 ")
    void givenResponseService_whenReceiveUntrackMessageWithZeroAndLessNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 0");
        when(client.getAllLinksForChat(userId)).thenReturn(new ListLinksResponse(List.of(new LinkResponse(
            0L,
            URI.create("https://vk.com")
        )), 0));

        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verify(client, times(1)).getAllLinksForChat(userId);

        Assertions.assertEquals(
            "Вы ввели число 0. У вас нет ссылки под таким номером. Полный список ссылок с номерами можно посмотреть при помощи команды /list\n",
            res
        );
    }

    @Test
    @DisplayName("Получение сообщения /untrack 2 и списка с меньшим колличеством ссылок")
    void givenResponseService_whenReceiveUntrackMessageWithCorrectNumberAndGetAllLinksCountLessThenNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 2");
        when(client.getAllLinksForChat(userId)).thenReturn(new ListLinksResponse(List.of(new LinkResponse(
            0L,
            URI.create("https://vk.com")
        )), 0));
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verify(client, times(1)).getAllLinksForChat(userId);

        Assertions.assertEquals(
            "Вы ввели число 2. У вас нет ссылки под таким номером. Полный список ссылок с номерами можно посмотреть при помощи команды /list\n",
            res
        );
    }

    @Test
    @DisplayName("Получение сообщения /untrack 1")
    void givenResponseService_whenReceiveUntrackMessageWithCorrectNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 1");
        when(client.getAllLinksForChat(userId)).thenReturn(new ListLinksResponse(List.of(new LinkResponse(
            0L,
            URI.create("https://vk.com")
        )), 0));
        when(client.removeLink(eq(userId), any())).thenReturn(new LinkResponse(
            0L,
            URI.create("https://vk.com")
        ));
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verify(client, times(1)).getAllLinksForChat(userId);

        Assertions.assertEquals(
            "Успешно убрали ссылку\n",
            res
        );
    }

    @Test
    @DisplayName("Получение сообщения /untrack 1 и ошибки при удалении")
    void givenResponseService_whenReceiveUntrackMessageWithCorrectNumberAndRemoveException_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 1");
        when(client.getAllLinksForChat(userId)).thenReturn(new ListLinksResponse(List.of(new LinkResponse(
            0L,
            URI.create("https://vk.com")
        )), 0));
        when(client.removeLink(eq(userId), any())).thenThrow(new ApiErrorResponseException("ошибочка"));
        clearInvocations(client);

        var res = untrackCommand.handle(update);

        verify(client, times(1)).getAllLinksForChat(userId);
        verify(client, times(1)).removeLink(any(), any());

        Assertions.assertEquals(
            "ошибочка",
            res
        );
    }
}
