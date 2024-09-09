package edu.java.services.http;

import edu.java.clients.BotClient;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.services.LinkUpdateSenderService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScrapperHttpUpdateService implements LinkUpdateSenderService {

    private final BotClient client;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        client.sendUpdate(linkUpdateRequest);
    }
}
