package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.entity.repository.UserLinksRepository;

public class ListCommand extends Command {

    private final UserLinksRepository userLinksRepository;

    public ListCommand(UserLinksRepository userLinksRepository) {
        super("/list", "список всех ссылок");
        this.userLinksRepository = userLinksRepository;
    }

    @Override
    public String handle(Update update) {

        var userLinks = userLinksRepository.getLinks(update.message().chat().id());
        if (userLinks.isEmpty()) {
            return "У вас пока нет ссылок\n";
        }
        StringBuilder sb = new StringBuilder("Ваши ссылки \n");
        for (int i = 0; i < userLinks.size(); i++) {
            sb.append(i + 1).append(") ").append(userLinks.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
