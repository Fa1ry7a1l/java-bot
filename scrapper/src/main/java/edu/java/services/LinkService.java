package edu.java.services;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface LinkService {
    ListLinksResponse getAllLinks(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest request);

    LinkResponse removeLink(Long tgChatId, RemoveLinkRequest request);

    List<Link> findUpdateableLinks();

    void updateLink(Link link);

    List<Chat> chatsByLink(Link link);
}
