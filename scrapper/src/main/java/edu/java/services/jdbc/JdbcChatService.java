package edu.java.services.jdbc;

import edu.java.entity.Chat;
import edu.java.entity.repository.ChatRepository;
import edu.java.exceptions.TelegramChatAlreadyRegisteredException;
import edu.java.services.ChatService;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class JdbcChatService implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public void register(Long id) {
        Chat c = new Chat(id, OffsetDateTime.now());
        if (!chatRepository.exists(c)) {
            chatRepository.add(c);
        } else {
            throw new TelegramChatAlreadyRegisteredException(id);
        }
    }

    @Override
    public void remove(Long id) {

        Chat c = new Chat(id, OffsetDateTime.now());
        chatRepository.remove(c);
    }
}
