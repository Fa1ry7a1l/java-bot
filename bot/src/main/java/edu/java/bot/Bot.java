package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.CommandHandler;
import edu.java.bot.configuration.ApplicationConfig;
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

    private final CommandHandler commandHandler;

    private final ResponseService responseService;

    @Autowired
    public Bot(ApplicationConfig config, CommandHandler commandHandler, ResponseService responseService) {
        this.telegramBot = new TelegramBot(config.telegramToken());

        this.commandHandler = commandHandler;
        this.responseService = responseService;
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

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            if (update.message() != null) {
                LOGGER.info("Сообщение : " + update.message().text());

                var response = responseService.getAnswer(update);
                var responseArray = response.split(SPLIT_TOO_LONG_ANSWERS_REGEX);
                for (var message : responseArray) {
                    SendMessage sendMessage =
                        new SendMessage(update.message().chat().id(), message);

                    telegramBot.execute(sendMessage);
                }
                LOGGER.info("Закончили : " + update.message().text());

            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SetMyCommands setCommands() {

        var commands = commandHandler.getCommands().values().stream().toList();
        BotCommand[] botCommands = new BotCommand[commands.size()];
        for (int i = 0; i < commands.size(); i++) {
            botCommands[i] = new BotCommand(commands.get(i).command, commands.get(i).description);
        }
        return new SetMyCommands(botCommands);
    }
}
