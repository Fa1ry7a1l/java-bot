package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.CommandHandler;
import edu.java.bot.entity.repository.UserLinksRepository;
import edu.java.bot.entity.repository.UserRepository;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
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
import static org.mockito.Mockito.never;
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

    private long userId;

    CommandHandler commandHandler;

    @Mock
    UserRepository userRepository;
    @Mock
    UserLinksRepository userLinksRepository;

    ResponseService responseService;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        commandHandler = new CommandHandler(userLinksRepository);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(userId);
        responseService = new ResponseService(userRepository, commandHandler);
    }

    @Test
    @DisplayName("Получение сообщения /help")
    void givenResponseService_whenReceiveHelpMessage_thenReturnHelpText() {
        when(message.text()).thenReturn("/help");

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("""
                /start - Запоминание пользователя
                /track - Добавляет в отслеживание сайт. Обязательно нужно ссылку с упоминанием протокола. Например /track https://vk.com
                /untrack - перестает отслеживать страницу. Например /untrack 1
                /list - список всех ссылок
                /help - Выводит список команд
                """, res);
    }

    @Test
    @DisplayName("Получение сообщения /start")
    void givenResponseService_whenReceiveStartMessage_thenReturnStartTextAndRegisterUser() {
        when(message.text()).thenReturn("/start");
        when(userRepository.getUser(any())).thenReturn(Optional.empty());

        var res = responseService.getAnswer(update);

        verify(userRepository, times(1)).getUser(userId);
        verify(userRepository, times(1)).put(eq(userId), any());

        Assertions.assertEquals("Я вас запомнил\n", res);
    }

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

    @Test
    @DisplayName("Получение сообщения с отсутствующей командой")
    void givenResponseService_whenReceiveMessageWithIncorrectCommand_thenReturnSpecialMessage() {
        when(message.text()).thenReturn("/Привет, как дела?");

        var res = responseService.getAnswer(update);

        Assertions.assertEquals("Команда не распознана, "
            + "список команд можно увидеть при помощи /help\n", res);
    }
}
