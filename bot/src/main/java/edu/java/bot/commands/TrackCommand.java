package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.LinkChecker;
import edu.java.bot.entity.repository.UserLinksRepository;
import java.net.URI;

public class TrackCommand extends Command {

    LinkChecker linkChecker;
    UserLinksRepository userLinksRepository;

    public TrackCommand(UserLinksRepository userLinksRepository, LinkChecker linkChecker) {
        super(
            "/track",
            "Добавляет в отслеживание сайт. Обязательно нужно ссылку с упоминанием протокола. Например /track https://vk.com"
        );
        this.userLinksRepository = userLinksRepository;

        this.linkChecker = linkChecker;
    }

    @Override
    public String handle(Update update) {
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

        userLinksRepository.addLink(update.message().chat().id(), uri);
        return "Успешно добавили\n";
    }
}
