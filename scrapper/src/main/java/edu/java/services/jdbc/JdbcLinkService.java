package edu.java.services.jdbc;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import edu.java.entity.repository.ChatRepository;
import edu.java.entity.repository.LinkRepository;
import edu.java.exceptions.DeletingNotExistingUrlException;
import edu.java.exceptions.ReaddingLinkException;
import edu.java.exceptions.UserNotFoundException;
import edu.java.services.LinkService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JdbcLinkService implements LinkService {

    private final ChatRepository chatRepository;
    private final LinkRepository linksRepository;

    @Override
    public ListLinksResponse getAllLinks(Long tgChatId) {
    public ListLinksResponse findAllLinks(Long tgChatId) {
        var chat = chatRepository.find(tgChatId);
        if (chat == null) {
            throw new UserNotFoundException(tgChatId);
        }
        var allUserLinks = linksRepository.findAllChatLinks(chat);
        return new ListLinksResponse(allUserLinks.stream().map(link -> new LinkResponse(link.getId(), link.getUrl()))
            .toList(), allUserLinks.size());

    }

    @Transactional
    @Override
    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        Chat chat = chatRepository.find(tgChatId);
        if (chat == null) {
            throw new UserNotFoundException(tgChatId);
        }
        Link link = linksRepository.findByUrl(request.link().toString());
        if (link == null) {
            link = linksRepository.add(new Link(0L, request.link(), "", OffsetDateTime.MIN));
        }

        boolean res = linksRepository.addChatLink(chat, link);

        if (!res) {
            throw new ReaddingLinkException(tgChatId, request.link());
        }
        return new LinkResponse(link.getId(), link.getUrl());
    }

    @Transactional
    @Override
    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest request) {
        Chat chat = chatRepository.find(tgChatId);
        if (chat == null) {
            throw new UserNotFoundException(tgChatId);
        }

        Link link = linksRepository.findByUrl(request.url().toString());
        if (link == null) {
            throw new DeletingNotExistingUrlException(tgChatId, request.url());
        }

        linksRepository.removeChatLink(chat, link);

        return new LinkResponse(link.getId(), link.getUrl());

    }

    @Override
    public List<Link> findUpdateableLinks() {
        return linksRepository.findMoreThenFifeMinutesLaterUpdated();
    }

    @Override
    public void updateLink(Link link) {
        linksRepository.updateLink(link);
    }

    @Override
    public List<Chat> chatsByLink(Link link) {
        return linksRepository.findChatsByLink(link);
    }
}
