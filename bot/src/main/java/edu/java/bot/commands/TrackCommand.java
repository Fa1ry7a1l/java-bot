package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.LinkChecker;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.AddLinkRequest;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

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

        var uriProcessError = processUri(uri);
        if (!uriProcessError.isEmpty()) {
            return uriProcessError;
        }

        try {
            var response = client.addLink(update.message().chat().id(), new AddLinkRequest(uri));
            return "Успешно добавили\n";
        } catch (ApiErrorResponseException e) {
            LOGGER.debug("track пользователю " + id + " вернуло ошибку " + e.getDescription());
            return e.getDescription();
        }

    }

    private String processUri(URI uri) {
        if (uri == null) {
            return "Некорректная ссылка\n";
        }

        if (uri.getHost() == null) {
            return "Ссылка должна содержать протокол: http:// или https://\n";
        }
        return "";
    }
}
