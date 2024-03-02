package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component public class UntrackCommand extends Command {

    static final Logger LOGGER = LogManager.getLogger(UntrackCommand.class.getName());

    private final ScrapperClient client;

    public UntrackCommand(ScrapperClient client) {
        super("/untrack", "перестает отслеживать страницу. " + "Например /untrack 1");
        this.client = client;
    }

    @Override public String handle(Update update) {
        Long id = update.message().chat().id();
        String text = update.message().text();
        var textArray = text.split("\s+");
        if (textArray.length == 1) {
            return "Добавьте номер ссылки. Например /untrack 1\n";
        }
        int linkNumber;

        try {
            linkNumber = Integer.parseInt(textArray[1]);

        } catch (NumberFormatException e) {
            LOGGER.info("не удалось распознать число в " + textArray[1]);
            return "Не удалось распознать номер ссылки. Команда должна выглядить: /untrack 1\n";
        }

        var userLinks = getUserLinks(id);

        if (userLinks.size() < linkNumber || linkNumber <= 0) {
            LOGGER.info("Полученное число - " + linkNumber + ". У пользователя нет ссылки под таким номером");
            return "Вы ввели число " + linkNumber + ". У вас нет ссылки под таким номером. "
                + "Полный список ссылок с номерами можно посмотреть " + "при помощи команды /list\n";
        }

        return removeLink(id, userLinks, linkNumber);

    }

    @Nullable private List<LinkResponse> getUserLinks(Long id) {
        return client.getAllLinksForChat(id).map(response -> Objects.requireNonNull(response.getBody()).links())
            .onErrorReturn(new ListLinksResponse(new ArrayList<>(), 0).links()).block();
    }

    private String removeLink(Long id, List<LinkResponse> userLinks, int linkNumber) {
        return client.removeLink(id, new RemoveLinkRequest(userLinks.get(linkNumber - 1).url())).map(response -> {
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return "Успешно убрали ссылку\n";
            }

            LOGGER.info("removeLink " + id + " вернуло код " + response.getStatusCode());
            return "Что то пошло не так";
        }).onErrorResume(ApiErrorResponseException.class, exception -> {
            LOGGER.debug(
                "removeLink пользователю " + id + " вернуло ошибку " + exception.getApiErrorResponse().description());
            return Mono.just(exception.getApiErrorResponse().description());
        }).block();
    }
}
