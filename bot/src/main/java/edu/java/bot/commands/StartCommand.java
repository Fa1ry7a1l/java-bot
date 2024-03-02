package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component public class StartCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(StartCommand.class);

    private final ScrapperClient client;

    public StartCommand(ScrapperClient client) {
        super("/start", "Запоминание пользователя");
        this.client = client;
    }

    @Override public String handle(Update update) {
        Long id = update.message().chat().id();
        return client.registerChat(id).map(response -> {
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return "Я вас запомнил\n";
            }

            LOGGER.info("registerChat " + id + " вернуло код " + response.getStatusCode());
            return "Что то пошло не так";
        }).onErrorResume(ApiErrorResponseException.class, exception -> {
            LOGGER.debug(
                "registerChat пользователю " + id + " вернуло ошибку " + exception.getApiErrorResponse().description());
            return Mono.just(exception.getApiErrorResponse().description());
        }).block();

    }
}
