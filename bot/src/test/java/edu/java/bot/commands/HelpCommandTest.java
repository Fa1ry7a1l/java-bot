package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.LinkChecker;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.bot.services.ResponseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class HelpCommandTest {
    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private Chat chat;

    @Mock
    ScrapperClient client;

    private HelpCommand helpCommand;

    @Mock
    private ListCommand listCommand;

    private StartCommand startCommand;

    private TrackCommand trackCommand;

    private UntrackCommand untrackCommand;

    private long userId;


    @BeforeEach
    public void setUp() {
        List<Command> allAvailableCommands = new ArrayList<>();
        listCommand = new ListCommand(client);
        startCommand = new StartCommand(client);
        trackCommand = new TrackCommand(client, new LinkChecker());
        untrackCommand=new UntrackCommand(client);

        allAvailableCommands.add(listCommand);
        allAvailableCommands.add(startCommand);
        allAvailableCommands.add(trackCommand);
        allAvailableCommands.add(untrackCommand);
        helpCommand = new HelpCommand(allAvailableCommands);


        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(userId);
    }

    @Test
    @DisplayName("Получение сообщения /help")
    void givenResponseService_whenReceiveHelp_thenReturnSpecialMessage() {

        var res = helpCommand.handle(update);

        Assertions.assertEquals("/list - список всех ссылок\n" +
            "/start - Запоминание пользователя\n" +
            "/track - Добавляет в отслеживание сайт. Обязательно нужно ссылку с упоминанием протокола. Например /track https://vk.com\n" +
            "/untrack - перестает отслеживать страницу. Например /untrack 1\n" +
            "/help - Выводит список команд\n", res);
    }
}
