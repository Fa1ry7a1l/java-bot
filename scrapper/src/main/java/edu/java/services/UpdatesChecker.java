package edu.java.services;

import edu.java.clients.BotClient;
import edu.java.clients.GitHubClient;
import edu.java.clients.StackOverflowClient;
import edu.java.clients.dto.GitHubDTO;
import edu.java.clients.dto.StackOverflowDTO;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UpdatesChecker {

    private static final Logger LOGGER = LogManager.getLogger(UpdatesChecker.class.getName());

    private final LinkService linkService;
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;


    public void startAllLicksCheck() {
        List<Link> links = linkService.findUpdateableLinks();

        for (var link : links) {
            var uri = link.getUrl();
            if (uri.toString().startsWith("https://github.com")) {
                workGithub(link, uri);

            } else if (uri.toString().startsWith("https://stackoverflow.com/questions/")) {
                workStackOverflow(link, uri);
            } else {
                LOGGER.error("некорректная ссылка. не подходит под клиентов " + link.getUrl());
            }
        }
    }

    private void workStackOverflow(Link link, URI uri) {
        Pattern p = Pattern.compile("https://stackoverflow\\.com/questions/([^/]+)/");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            LOGGER.error("не удалось найти куда репозиторий в ссылке stackoverflow - " + uri);
        }

        StackOverflowDTO stackOverflowDTO = stackOverflowClient.getQuestionsInfo(m.group(1));
        if (stackOverflowDTO.items().getFirst().lastActivityDate().isAfter(link.getUpdatedAt())) {
            Link updatedLink = link;
            updatedLink.setUpdatedAt(stackOverflowDTO.items().getFirst().lastActivityDate());
            linkService.updateLink(updatedLink);
            List<Chat> chats = chatService.findChatsByLink(link);
            if (!chats.isEmpty()) {
                botClient.sendUpdate(new LinkUpdateRequest(
                    updatedLink.getId(),
                    updatedLink.getUrl(),
                    updatedLink.getDescription(),
                    chats.stream().map(Chat::getId).collect(
                        Collectors.toList())
                ));
            }
        }
    }

    private void workGithub(Link link, URI uri) {
        Pattern p = Pattern.compile("https://github\\.com/([^/]+/[^/]+)");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            LOGGER.error("не удалось найти куда репозиторий в ссылке git - " + uri);
        }

        GitHubDTO gitHubDTO = gitHubClient.getQuestionsInfo(m.group(1));
        if (gitHubDTO.lastActivityDate().isAfter(link.getUpdatedAt())) {
            Link updatedLink = link;
            updatedLink.setUpdatedAt(gitHubDTO.lastActivityDate());
            linkService.updateLink(updatedLink);
            List<Chat> chats = chatService.findChatsByLink(link);
            if (!chats.isEmpty()) {
                botClient.sendUpdate(new LinkUpdateRequest(
                    updatedLink.getId(),
                    updatedLink.getUrl(),
                    updatedLink.getDescription(),
                    chats.stream().map(Chat::getId).collect(
                        Collectors.toList())
                ));
            }
        }
    }
}
