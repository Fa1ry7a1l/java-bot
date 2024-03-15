package edu.java.services;

import edu.java.entity.User;
import edu.java.entity.repository.UserLinksRepository;
import edu.java.entity.repository.UserRepository;
import edu.java.exceptions.TelegramChatAlreadyRegisteredException;
import edu.java.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ChatService {

    private UserRepository userRepository;
    private UserLinksRepository userLinksRepository;

    public void register(Long id) {
        if (userRepository.getUser(id).isPresent()) {
            throw new TelegramChatAlreadyRegisteredException(id);
        }
        userRepository.put(id, new User(id));
        userLinksRepository.createUser(id);
    }

    public void delete(Long id) {

        var result = userRepository.delete(id);
        if (!result) {
            throw new UserNotFoundException(id);
        }
    }
}
