package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.exceptions.ApiErrorResponseException;
import edu.java.dtos.LinkResponse;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
        return client.getAllLinksForChat(id)
            .map(response -> {
                if (!HttpStatus.OK.equals(response.getStatusCode())) {
                    LOGGER.info(
                        "получена ошибка при запросе ссылок у пользователя " + id + " " + response.getStatusCode()
                            + " " + response.getBody());
                }
                if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody() != null
                    && response.getBody().links() != null) {
                    return processLinks(response.getBody().links());
                }
                LOGGER.info("addLink " + id + " вернуло код " + response.getStatusCode());
                return "Что то пошло не так";
            })
            .onErrorResume(ApiErrorResponseException.class, exception -> {
                LOGGER.debug("addLink пользователю " + id + " вернуло ошибку " + exception.getApiErrorResponse()
                    .description());
                return Mono.just(exception.getApiErrorResponse()
                    .description());
            }).block();
    }

    private String processLinks(List<LinkResponse> links) {
        if (links.isEmpty()) {
            return "У вас пока нет ссылок\n";
        }
        StringBuilder sb = new StringBuilder("Ваши ссылки \n");
        for (int i = 0; i < links.size(); i++) {
            sb.append(i + 1).append(") ").append(links.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
