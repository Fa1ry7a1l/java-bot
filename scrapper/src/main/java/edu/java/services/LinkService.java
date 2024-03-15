package edu.java.services;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.LinkResponse;
import edu.java.dtos.ListLinksResponse;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.repository.UserLinksRepository;
import edu.java.entity.repository.UserRepository;
import edu.java.exceptions.DeletingNotExistingUrlException;
import edu.java.exceptions.ReaddingLinkException;
import edu.java.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LinkService {

    private UserRepository userRepository;
    private UserLinksRepository userLinksRepository;

    public ListLinksResponse getAllLinks(Long tgChatId) {
        if (userRepository.getUser(tgChatId).isEmpty()) {
            throw new UserNotFoundException(tgChatId);
        }
        var res = userLinksRepository.getLinks(tgChatId);
        return new ListLinksResponse(res.stream().map(uri -> new LinkResponse(0L, uri)).toList(), res.size());
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest request) {
        if (userRepository.getUser(tgChatId).isEmpty()) {
            throw new UserNotFoundException(tgChatId);
        }
        var res = userLinksRepository.add(tgChatId, request.link());
        if (!res) {
            throw new ReaddingLinkException(tgChatId, request.link());
        }
        return new LinkResponse(0L, request.link());
    }

    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest request) {
        if (userRepository.getUser(tgChatId).isEmpty()) {
            throw new UserNotFoundException(tgChatId);
        }
        var res = userLinksRepository.remove(tgChatId, request.url());
        if (!res) {
            throw new DeletingNotExistingUrlException(tgChatId, request.url());
        }
        return new LinkResponse(0L, request.url());

    }
}
