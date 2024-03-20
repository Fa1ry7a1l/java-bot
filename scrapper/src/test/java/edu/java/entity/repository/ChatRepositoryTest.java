package edu.java.entity.repository;

import edu.java.entity.Chat;
import edu.java.scrapper.IntegrationTest;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@SpringBootTest
class ChatRepositoryTest extends IntegrationTest {

    @Autowired
    ChatRepository chatRepository;

    @DisplayName("проверка добавления порльзователя")
    @Test
    @Transactional
    @Rollback
    void givenEmptyRepository_whenAddChat_thenEntityAppearsInDB() {
        Chat c = new Chat(100L, OffsetDateTime.now());

        assertTrue(chatRepository.findAll().isEmpty());
        chatRepository.add(c);

        var res = chatRepository.findAll();
        assertEquals(1, res.size());
        var chatFromBD = res.getFirst();
        assertEquals(c.getId(), chatFromBD.getId());
        var a = c.getRegisteredAt().minusNanos(c.getRegisteredAt().getNano());
        var b = chatFromBD.getRegisteredAt().minusNanos(chatFromBD.getRegisteredAt().getNano());

        assertTrue(a.isEqual(b));

    }

    @DisplayName("проверка добавления уже добавленного пользователя")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenAddChatAlreadyExists_thenException() {
        Chat c = new Chat(100L, OffsetDateTime.now());

        chatRepository.add(c);

        var res = chatRepository.findAll();
        assertEquals(1, res.size());
        assertThrows(RuntimeException.class, () -> chatRepository.add(c));

    }

    @DisplayName("проверка find all на пустом массиве")
    @Test
    @Transactional
    @Rollback
    void givenEmptyRepository_whenFindAll_thenEmptyList() {

        var res = chatRepository.findAll();
        assertTrue(res.isEmpty());
    }

    @DisplayName("проверка find all на не пустом массиве")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenFindAll_thenEmptyList() {

        var c1 = new Chat(100L, OffsetDateTime.now());
        var c2 = new Chat(102L, OffsetDateTime.now());

        chatRepository.add(c1);
        chatRepository.add(c2);

        var res = chatRepository.findAll();
        assertEquals(2, res.size());
    }

    @DisplayName("remove на пустом массиве")
    @Test
    @Transactional
    @Rollback
    void givenEmptyRepository_whenRemoveChat_thenReturn0() {

        var c1 = new Chat(100L, OffsetDateTime.now());

        var res = chatRepository.remove(c1);
        Assertions.assertNull(res);
        assertTrue(chatRepository.findAll().isEmpty());
    }

    @DisplayName("remove на не пустом массиве")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenRemoveChatExists_thenReturn0() {

        var c1 = new Chat(100L, OffsetDateTime.now());

        chatRepository.add(c1);

        var res = chatRepository.remove(c1);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(c1.getId(), res.getId());
        assertTrue(chatRepository.findAll().isEmpty());
    }

    @DisplayName("exists на не пустом массиве, где элемента нет")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenExistsNonExistedChat_thenReturnFalse() {

        var c1 = new Chat(100L, OffsetDateTime.now());
        var c2 = new Chat(101L, OffsetDateTime.now());
        var c3 = new Chat(200L, OffsetDateTime.now());

        chatRepository.add(c1);
        chatRepository.add(c2);

        var res = chatRepository.exists(c3);

        assertFalse(res);
    }

    @DisplayName("exists на не пустом массиве, где элемент есть")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenExistsExistedChat_thenReturnTrue() {

        var c1 = new Chat(100L, OffsetDateTime.now());
        var c2 = new Chat(101L, OffsetDateTime.now());

        chatRepository.add(c1);
        chatRepository.add(c2);

        var res = chatRepository.exists(c1);

        assertTrue(res);
    }

    @DisplayName("find на не пустом массиве, где элемент есть")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenFindExistedChat_thenReturnChat() {

        var c1 = new Chat(100L, OffsetDateTime.now());
        var c2 = new Chat(101L, OffsetDateTime.now());

        chatRepository.add(c1);
        chatRepository.add(c2);

        var res = chatRepository.find(c1.getId());

        assertNotNull(res);
        assertEquals(c1.getId(), res.getId());
    }

    @DisplayName("find на  массиве, где нет элемента")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyRepository_whenFindExistedChat_thenReturnNull() {

        var c1 = new Chat(100L, OffsetDateTime.now());
        var c2 = new Chat(101L, OffsetDateTime.now());

        chatRepository.add(c1);

        var res = chatRepository.find(c2.getId());

        Assertions.assertNull(res);
    }

}
