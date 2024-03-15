package edu.java.bot.services;

import edu.java.bot.Bot;
import edu.java.dtos.LinkUpdateRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private Bot bot;

    private NotificationService notificationService;

    @BeforeEach
    public void beforeEach() {
        clearInvocations(bot);
        notificationService = new NotificationService(bot);
    }

    @Test
    @DisplayName("Отправка пользователю сообщения ")
    void givenResponseService_whenReceiveUpdateMessage_thenSendItViaBot() {
        LinkUpdateRequest request = new LinkUpdateRequest(0L, URI.create("https://vk.com"), "привет", List.of(1L, 2L));

        notificationService.sendUpdateNotification(request);

        verify(bot, times(1)).sendMessage(eq(1L), eq("""
            ресурс
            ```привет```
            был обновлен
            """));
        verify(bot, times(1)).sendMessage(eq(2L), eq("""
            ресурс
            ```привет```
            был обновлен
            """));
        assertThrows(NoInteractionsWanted.class, () ->verifyNoInteractions(bot));

    }

}
