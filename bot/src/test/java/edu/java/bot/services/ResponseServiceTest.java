package edu.java.bot.services;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.services.ResponseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseServiceTest {
    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    @Mock
    private HelpCommand helpCommand;

    @Mock
    private ListCommand listCommand;

    @Mock
    private StartCommand startCommand;

    @Mock
    private TrackCommand trackCommand;

    @Mock
    private UntrackCommand untrackCommand;

    private long userId;

    private ResponseService responseService;

    @BeforeEach
    public void setUp() {
        List<Command> allAvailableCommands = new ArrayList<>();
        lenient().when(helpCommand.getCommand()).thenReturn("/help");
        lenient().when(helpCommand.handle(any())).thenReturn("/help");
        lenient().when(listCommand.getCommand()).thenReturn("/list");
        lenient().when(listCommand.handle(any())).thenReturn("/list");
        lenient().when(startCommand.getCommand()).thenReturn("/start");
        lenient().when(startCommand.handle(any())).thenReturn("/start");
        lenient().when(trackCommand.getCommand()).thenReturn("/track");
        lenient().when(trackCommand.handle(any())).thenReturn("/track");
        lenient().when(untrackCommand.getCommand()).thenReturn("/untrack");
        lenient().when(untrackCommand.handle(any())).thenReturn("/untrack");

        allAvailableCommands.add(listCommand);
        allAvailableCommands.add(startCommand);
        allAvailableCommands.add(trackCommand);
        allAvailableCommands.add(untrackCommand);
        allAvailableCommands.add(helpCommand);

        responseService = new ResponseService(allAvailableCommands);

        for (var command : allAvailableCommands) {
            clearInvocations(command);
        }

        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(userId);
    }

    @Test
    @DisplayName("Получение сообщения /help")
    void givenResponseService_whenReceiveHelpMessage_thenReturnHelpText() {
        when(message.text()).thenReturn("/help");

        var res = responseService.getAnswer(update);

        verify(helpCommand, times(1)).handle(any());
        Assertions.assertEquals(res,helpCommand.getCommand());

    }
    @Test
    @DisplayName("Получение сообщения /start")
    void givenResponseService_whenReceiveStartMessage_thenReturnHelpText() {
        when(message.text()).thenReturn("/start");

        var res = responseService.getAnswer(update);

        verify(startCommand, times(1)).handle(any());
        Assertions.assertEquals(res,startCommand.getCommand());

    }

    @Test
    @DisplayName("Получение сообщения /list")
    void givenResponseService_whenReceiveListMessage_thenReturnHelpText() {
        when(message.text()).thenReturn("/list");

        var res = responseService.getAnswer(update);

        verify(listCommand, times(1)).handle(any());
        Assertions.assertEquals(res,listCommand.getCommand());
    }

    @Test
    @DisplayName("Получение сообщения /track")
    void givenResponseService_whenReceiveTrackMessage_thenReturnHelpText() {
        when(message.text()).thenReturn("/track");

        var res = responseService.getAnswer(update);

        verify(trackCommand, times(1)).handle(any());
        Assertions.assertEquals(res,trackCommand.getCommand());
    }

    @Test
    @DisplayName("Получение сообщения /untrack")
    void givenResponseService_whenReceiveUntrackMessage_thenReturnHelpText() {
        when(message.text()).thenReturn("/untrack");

        var res = responseService.getAnswer(update);

        verify(untrackCommand, times(1)).handle(any());
        Assertions.assertEquals(res,untrackCommand.getCommand());
    }

    @Test
    @DisplayName("Получение сообщения с отсутствующей командой")
    void givenResponseService_whenReceiveMessageWithIncorrectCommand_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/Привет, как дела?");

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("Команда не распознана, "
            + "список команд можно увидеть при помощи /help\n", res);
    }

    /*
    @Test
    @DisplayName("Получение сообщения /list без добавленных ссылок")
    void givenResponseService_whenReceiveListMessageWithoutLinks_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/list");
        when(userLinksRepository.getLinks(any())).thenReturn(new ArrayList<>());

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("У вас пока нет ссылок\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /list, ссылки уже есть")
    void givenResponseService_whenReceiveListMessageWithLinks_thenReturnSpecialMessage() {
        ArrayList<URI> uris = new ArrayList<>();
        uris.add(URI.create("https://vk.com"));
        uris.add(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        when(message.text()).thenReturn("/list");
        when(userLinksRepository.getLinks(any())).thenReturn(uris);

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("""
            Ваши ссылки\s
            1) https://vk.com
            2) https://www.youtube.com/watch?v=dQw4w9WgXcQ
            """, res);
    }

    @Test
    @DisplayName("Получение сообщения /track, без ссылки")
    void givenResponseService_whenReceiveTrackMessageWithoutLink_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track");

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("Введите ссылку с упоминанием протокола. "
            + "Например /track https://vk.com\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /track, с некорректной ссылкой")
    void givenResponseService_whenReceiveTrackMessageWithIncorrectLink_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track http://vk.com!*+^");

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("Некорректная ссылка\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /track, с ссылкой без протакола")
    void givenResponseService_whenReceiveTrackMessageWithLinkWithoutProtocol_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track vk.com");

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("Ссылка должна содержать протокол: http:// или https://\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /track, с корректной ссылкой")
    void givenResponseService_whenReceiveTrackMessageWithCorrectLink_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/track https://vk.com");
        clearInvocations(userRepository);

        var res = responseService.getAnswer(update);
        verify(userLinksRepository, times(1)).addLink(eq(userId), any());

        Assertions.assertEquals("Успешно добавили\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /untrack, с корректным номером")
    void givenResponseService_whenReceiveUntrackMessageWithCorrectNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 1");
        ArrayList<URI> uris = new ArrayList<>();
        uris.add(URI.create("https://vk.com"));
        uris.add(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        when(userLinksRepository.getLinks(userId)).thenReturn(uris);
        clearInvocations(userRepository);

        var res = responseService.getAnswer(update);
        Assertions.assertEquals(uris.size(), 1);

        Assertions.assertEquals("Успешно убрали ссылку\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /untrack, с слишком большим номером")
    void givenResponseService_whenReceiveUntrackMessageWithTooBigNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 125");
        ArrayList<URI> uris = new ArrayList<>();
        uris.add(URI.create("https://vk.com"));
        uris.add(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        when(userLinksRepository.getLinks(userId)).thenReturn(uris);
        clearInvocations(userRepository);

        var res = responseService.getAnswer(update);
        Assertions.assertEquals(uris.size(), 2);

        Assertions.assertEquals("Вы ввели число 125. "
            + "У вас нет ссылки под таким номером. "
            + "Полный список ссылок с номерами можно посмотреть "
            + "при помощи команды /list\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /untrack, с слишком маленьким номером")
    void givenResponseService_whenReceiveUntrackMessageWithTooSmallNumber_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/untrack 0");
        ArrayList<URI> uris = new ArrayList<>();
        uris.add(URI.create("https://vk.com"));
        uris.add(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
        when(userLinksRepository.getLinks(userId)).thenReturn(uris);
        clearInvocations(userRepository);

        var res = responseService.getAnswer(update);
        Assertions.assertEquals(uris.size(), 2);

        Assertions.assertEquals("Вы ввели число 0. "
            + "У вас нет ссылки под таким номером. "
            + "Полный список ссылок с номерами можно посмотреть "
            + "при помощи команды /list\n", res);
    }

    @Test
    @DisplayName("Получение сообщения /untrack, с нечитаемым номером")
    void givenResponseService_whenReceiveUntrackMessageWithUnrecognizableNumber_thenReturnSpecialMessage() {

        when(message.text()).thenReturn("/untrack 75a");
        clearInvocations(userRepository);

        var res = responseService.getAnswer(update);
        verify(userLinksRepository, never()).getLinks(any());

        Assertions.assertEquals("Не удалось распознать номер ссылки. "
            + "Команда должна выглядить: /untrack 1\n", res);
    }

    */
}
