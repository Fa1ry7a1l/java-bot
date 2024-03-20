package edu.java.services.jpa;

import edu.java.entity.Chat;
import edu.java.entity.repository.jpa.JPAChatRepository;
import edu.java.exceptions.TelegramChatAlreadyRegisteredException;
import edu.java.services.ChatService;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JPAChatService implements ChatService {

    private final JPAChatRepository jpaChatRepository;

    @Override
    public void register(Long id) {
        if (jpaChatRepository.findById(id).isPresent()) {
            throw new TelegramChatAlreadyRegisteredException(id);
        }

        Chat chat = new Chat(id, OffsetDateTime.now());
        jpaChatRepository.save(chat);
    }

    @Override
    public void remove(Long id) {
        Optional<Chat> optionalChat = jpaChatRepository.findById(id);
        if (optionalChat.isPresent()) {
            jpaChatRepository.delete(optionalChat.get());
        }
    }
}
