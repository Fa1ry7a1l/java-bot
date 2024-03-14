package edu.java.services.jdbc;

import edu.java.entity.Chat;
import edu.java.entity.repository.ChatRepository;
import edu.java.exceptions.TelegramChatAlreadyRegisteredException;
import edu.java.services.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcChatServiceTest {

    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @BeforeEach
    public void beforeEach() {
        chatService = new JdbcChatService(chatRepository);
    }

    @Test
    public void givenChatService_WhenAddNewChat_ThenReceiveChatWithId() {
        when(chatRepository.exists(any())).thenReturn(false);

        try {
            chatService.register(1L);
        } catch (TelegramChatAlreadyRegisteredException e) {
            Assertions.assertTrue(false);
        }

        verify(chatRepository, times(1)).exists(any());
        verify(chatRepository, times(1)).add(any());
        verifyNoMoreInteractions(chatRepository);
    }

    @Test
    public void givenChatService_WhenAddExistedChat_ThenException() {
        when(chatRepository.exists(any())).thenReturn(true);

        Assertions.assertThrows(TelegramChatAlreadyRegisteredException.class, () -> chatService.register(1L));

        verify(chatRepository, times(1)).exists(any());
        verifyNoMoreInteractions(chatRepository);

    }

    @Test
    public void givenChatService_WhenRemoveChat_ThenOk() {
        when(chatRepository.remove(any())).thenReturn(new Chat(1L, OffsetDateTime.now()));

        chatService.remove(1L);

        verify(chatRepository, times(1)).remove(any());
        verifyNoMoreInteractions(chatRepository);

    }

}
