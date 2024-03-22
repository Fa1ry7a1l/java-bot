package edu.java.services;

import edu.java.entity.Chat;
import edu.java.entity.Link;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface ChatService {
    void add(Long id);

    void remove(Long id);

    List<Chat> findChatsByLink(Link link);

}
