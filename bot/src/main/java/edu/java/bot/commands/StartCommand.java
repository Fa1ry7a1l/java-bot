package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component public class StartCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(StartCommand.class);

    private final ScrapperClient client;

    public StartCommand(ScrapperClient client) {
        super("/start", "Запоминание пользователя");
        this.client = client;
    }

    @Override public String handle(Update update) {
        Long id = update.message().chat().id();
        try {

            client.registerChat(id);
            return "Я вас запомнил\n";
        } catch (ApiErrorResponseException e) {
            LOGGER.debug(
                "registerChat пользователю " + id + " вернуло ошибку " + e.getDescription());
            return e.getDescription();
        }
    }
}
