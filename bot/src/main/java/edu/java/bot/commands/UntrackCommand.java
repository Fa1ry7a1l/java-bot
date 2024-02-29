package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.entity.repository.UserLinksRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UntrackCommand extends Command {

    static final Logger LOGGER = LogManager.getLogger(UntrackCommand.class.getName());

    private final UserLinksRepository userLinksRepository;

    public UntrackCommand(UserLinksRepository userLinksRepository) {
        super("/untrack", "перестает отслеживать страницу. "
            + "Например /untrack 1");
        this.userLinksRepository = userLinksRepository;
    }

    @Override
    public String handle(Update update) {
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

        var userLinks = userLinksRepository.getLinks(update.message().chat().id());
        if (userLinks.size() < linkNumber || linkNumber <= 0) {
            LOGGER.info("Полученное число - " + linkNumber + ". У пользователя нет ссылки под таким номером");
            return "Вы ввели число " + linkNumber
                + ". У вас нет ссылки под таким номером. "
                + "Полный список ссылок с номерами можно посмотреть "
                + "при помощи команды /list\n";
        }

        userLinks.remove(linkNumber - 1);

        return "Успешно убрали ссылку\n";
    }
}
