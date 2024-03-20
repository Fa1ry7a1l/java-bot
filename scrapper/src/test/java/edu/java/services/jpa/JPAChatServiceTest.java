package edu.java.services.jpa;

import edu.java.entity.Chat;
import edu.java.entity.repository.jpa.JPAChatRepository;
import edu.java.exceptions.TelegramChatAlreadyRegisteredException;
import edu.java.scrapper.IntegrationTest;
import edu.java.services.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class JPAChatServiceTest extends IntegrationTest {

    private ChatService chatService;

    @Autowired
    private JPAChatRepository jpaChatRepository;

    @BeforeEach
    public void beforeEach() {
        chatService = new JPAChatService(jpaChatRepository);
    }

    @Test
    @Transactional
    @Rollback
    public void givenChatService_WhenAddNewChat_ThenReceiveChatWithId() {
        //when(jpaChatRepository.findById(any())).thenReturn(Optional.empty());
        Chat c = null;
        try {
            chatService.register(1L);
        } catch (TelegramChatAlreadyRegisteredException e) {
            Assertions.assertTrue(false);
        }
        var chat = jpaChatRepository.findById(1L);
        Assertions.assertTrue(chat.isPresent());
        Assertions.assertEquals(1, chat.get().getId());
    }

    @Test
    @Transactional
    @Rollback
    public void givenChatService_WhenAddExistedChat_ThenException() {
        Chat c = new Chat();
        c.setId(1L);

        jpaChatRepository.save(c);

        Assertions.assertThrows(TelegramChatAlreadyRegisteredException.class, () -> chatService.register(1L));

    }

    @Test
    @Transactional
    @Rollback
    public void givenChatService_WhenRemoveChat_ThenOk() {

        Chat c = new Chat();
        c.setId(1L);
        jpaChatRepository.save(c);

        chatService.remove(1L);

        var res = jpaChatRepository.findById(1L).isEmpty();
        Assertions.assertTrue(res);

    }

    @Test
    @Transactional
    @Rollback
    public void givenChatService_WhenRemoveChatNotExists_ThenOk() {

        chatService.remove(1L);

        var res = jpaChatRepository.findById(1L).isEmpty();
        Assertions.assertTrue(res);

    }
}
