package edu.java;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.clients.dto.GitHubDTO;
import edu.java.clients.dto.StackOverflowDTO;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import edu.java.services.UpdatesChecker;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LinkCheckerScheduler {
    private static final Logger LOGGER = LogManager.getLogger(LinkCheckerScheduler.class.getName());

    private final UpdatesChecker updatesChecker;


    @Scheduled(fixedDelayString = "${app.scheduler.interval}",
               initialDelayString = "${app.scheduler.force-check-delay}")
    public void update() {
        LOGGER.debug("вызвали проверку ссылок");
        updatesChecker.startAllLicksCheck();
    }



}
