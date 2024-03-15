package edu.java.bot.services;

import edu.java.bot.Bot;
import edu.java.dtos.LinkUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

    private Bot bot;

    public void sendUpdateNotification(LinkUpdateRequest request) {
        String message = """
            ресурс
            ```%s```
            был обновлен
            """.formatted(request.description());

        for (Long id : request.tgChatIds()) {
            bot.sendMessage(id, message);
        }
    }
}
