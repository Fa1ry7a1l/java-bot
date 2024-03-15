package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.services.ResponseService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bot implements UpdatesListener {

    private static final Logger LOGGER = LogManager.getLogger(Bot.class.getName());
    public static final String SPLIT_TOO_LONG_ANSWERS_REGEX = "(?<=\\G.{250})";

    private final TelegramBot telegramBot;

    private final List<Command> allCommands;

    private final ResponseService responseService;

    @Autowired
    public Bot(ApplicationConfig config, List<Command> allCommands, ResponseService responseService) {
        this.telegramBot = new TelegramBot(config.telegramToken());

        this.allCommands = allCommands;
        this.responseService = responseService;

        LOGGER.debug(allCommands.size());
    }

    @PostConstruct
    public void start() {

        telegramBot.execute(setCommands());
        telegramBot.setUpdatesListener(this, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                LOGGER.error("Телеграм вернул код ошибки " + e.response().errorCode() + " с описанием '"
                    + e.response().description() + "'");
            } else {

                LOGGER.error("Вероятно, ошибка сети", e);
            }
        });

        LOGGER.info("завершили инициализацию");
    }

    public void sendMessage(Long id, String message) {
        var splitMessage = message.split(SPLIT_TOO_LONG_ANSWERS_REGEX);
        for (var messagePars : splitMessage) {
            SendMessage sendMessage =
                new SendMessage(id, messagePars).disableWebPagePreview(true);

            telegramBot.execute(sendMessage);
        }
    }

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            if (update.message() != null) {
                LOGGER.info("Сообщение : " + update.message().text());

                var response = responseService.getAnswer(update);
                var responseArray = response.split(SPLIT_TOO_LONG_ANSWERS_REGEX);
                for (var message : responseArray) {
                    SendMessage sendMessage =
                        new SendMessage(update.message().chat().id(), message).disableWebPagePreview(true);

                    telegramBot.execute(sendMessage);
                }
                LOGGER.info("Закончили : " + update.message().text());

            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SetMyCommands setCommands() {

        BotCommand[] botCommands = new BotCommand[allCommands.size()];
        for (int i = 0; i < allCommands.size(); i++) {
            botCommands[i] = new BotCommand(allCommands.get(i).getCommand(), allCommands.get(i).getDescription());
        }
        return new SetMyCommands(botCommands);
    }
}
