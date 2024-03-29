package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.LinkResponse;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends Command {
    private static final Logger LOGGER = LogManager.getLogger(ListCommand.class);

    private final ScrapperClient client;

    public ListCommand(ScrapperClient client) {
        super("/list", "список всех ссылок");
        this.client = client;
    }

    @Override
    public String handle(Update update) {
        Long id = update.message().chat().id();

        try {
            var linkListResponse = client.getAllLinksForChat(id);
            return processLinks(linkListResponse.links());
        } catch (ApiErrorResponseException e) {
            return e.getDescription();
        }

    }

    private String processLinks(List<LinkResponse> links) {
        if (links.isEmpty()) {
            return "У вас пока нет ссылок\n";
        }
        StringBuilder sb = new StringBuilder("Ваши ссылки \n");
        for (int i = 0; i < links.size(); i++) {
            sb.append(i + 1).append(") ").append(links.get(i).url().toString()).append("\n");
        }
        return sb.toString();
    }
}
