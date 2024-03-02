package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.LinkChecker;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.AddLinkRequest;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TrackCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(StartCommand.class);


    LinkChecker linkChecker;
    ScrapperClient client;

    public TrackCommand(ScrapperClient client, LinkChecker linkChecker) {
        super(
            "/track",
            "Добавляет в отслеживание сайт. Обязательно нужно ссылку с упоминанием протокола. Например /track https://vk.com"
        );
        this.client = client;
        this.linkChecker = linkChecker;
    }

    @Override
    public String handle(Update update) {
        Long id = update.message().chat().id();

        String text = update.message().text();
        var textArray = text.split("\s+");
        if (textArray.length == 1) {
            return
                "Введите ссылку с упоминанием протокола. Например /track https://vk.com\n";
        }

        URI uri = linkChecker.tryValidate(textArray[1]);

        if (uri == null) {
            return "Некорректная ссылка\n";
        }

        if (uri.getHost() == null) {
            return "Ссылка должна содержать протокол: http:// или https://\n";
        }

        return client.addLink(update.message().chat().id(), new AddLinkRequest(uri))
            .map(response -> {
                if (HttpStatus.OK.equals(response.getStatusCode())
                    && response.getBody() != null) {
                    return "Успешно добавили\n";
                }

                LOGGER.info("track " + id + " вернуло код " + response.getStatusCode());
                return "Что то пошло не так";
            })
            .onErrorResume(ApiErrorResponseException.class, exception -> {
                LOGGER.debug("track пользователю " + id + " вернуло ошибку " + exception.getApiErrorResponse()
                    .description());
                return Mono.just(exception.getApiErrorResponse()
                    .description());
            }).block();

    }
}
