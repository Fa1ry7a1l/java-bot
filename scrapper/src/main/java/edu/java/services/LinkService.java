package edu.java.services;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.Link;
import java.util.List;

public interface LinkService {
    ListLinksResponse findAllLinks(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest request);

    LinkResponse removeLink(Long tgChatId, RemoveLinkRequest request);

    List<Link> findUpdateableLinks();

    void updateLink(Link link);

}
