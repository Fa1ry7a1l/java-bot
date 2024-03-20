package edu.java.services.jpa;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import edu.java.entity.repository.jpa.JPAChatRepository;
import edu.java.entity.repository.jpa.JPALinkRepository;
import edu.java.exceptions.DeletingNotExistingUrlException;
import edu.java.exceptions.UserNotFoundException;
import edu.java.services.LinkService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JPALinkService implements LinkService {

    private static final Long MINUTES_TILL_OLD = 5L;

    private final JPAChatRepository jpaChatRepository;
    private final JPALinkRepository jpaLinkRepository;

    private Chat getChat(Long tgChatId) {
        Optional<Chat> optionalChat = jpaChatRepository.findById(tgChatId);
        if (optionalChat.isEmpty()) {
            throw new UserNotFoundException(tgChatId);
        }
        return optionalChat.get();
    }

    @Override
    public ListLinksResponse getAllLinks(Long tgChatId) {
        Chat chat = getChat(tgChatId);
        var links = chat.getLinks();
        return new ListLinksResponse(
            links.stream().map(link -> new LinkResponse(link.getId(), link.getUrl())).toList(),
            links.size()
        );
    }

    @Override
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        Chat chat = getChat(tgChatId);
        var link =
            jpaLinkRepository.findByUrl(request.link()).orElse(new Link(0L, request.link(), "", OffsetDateTime.now()));

        chat.getLinks().add(link);

        jpaChatRepository.save(chat);

        return new LinkResponse(link.getId(), link.getUrl());
    }

    @Override
    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest request) {

        Chat chat = getChat(tgChatId);

        Optional<Link> optionalLink = jpaLinkRepository.findByUrl(request.url());
        if (optionalLink.isEmpty()) {
            throw new DeletingNotExistingUrlException(tgChatId, request.url());
        }

        Link link = optionalLink.get();
        chat.getLinks().remove(link);
        jpaChatRepository.save(chat);

        return new LinkResponse(link.getId(), link.getUrl());
    }

    @Override
    public List<Link> findUpdateableLinks() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().minusMinutes(MINUTES_TILL_OLD);
        return jpaLinkRepository.findMoreThenOffsetUpdated(offsetDateTime);
    }

    @Override
    public void updateLink(Link link) {
        jpaLinkRepository.save(link);
    }

    @Override
    public List<Chat> chatsByLink(Link link) {
        return link.getChats().stream().toList();
    }
}
