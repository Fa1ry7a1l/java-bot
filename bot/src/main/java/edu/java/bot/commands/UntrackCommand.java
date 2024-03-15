package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.RemoveLinkRequest;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

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
        try {
            var linkListResponse = client.getAllLinksForChat(id);
            return linkListResponse.links();
        } catch (ApiErrorResponseException e) {
            return new ArrayList<>();
        }
    }

    private String removeLink(Long id, List<LinkResponse> userLinks, int linkNumber) {
        try {
            var response = client.removeLink(id, new RemoveLinkRequest(userLinks.get(linkNumber - 1).url()));
            return "Успешно убрали ссылку\n";
        } catch (ApiErrorResponseException e) {
            return e.getDescription();
        }
    }
}
