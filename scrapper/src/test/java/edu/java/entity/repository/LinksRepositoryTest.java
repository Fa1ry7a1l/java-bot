package edu.java.entity.repository;

import edu.java.entity.Chat;
import edu.java.entity.Link;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class LinksRepositoryTest extends IntegrationTest {

    @Autowired
    LinkRepository linkRepository;
    @Autowired
    ChatRepository chatRepository;

    @DisplayName("получение ссылок в пустой таблицу")
    @Test
    @Transactional
    @Rollback
    void givenEmptyChatLinksRepository_whenGetLinks_thenReceiveEmptyList() {
        var allLinks = linkRepository.findAll();

        Assertions.assertTrue(allLinks.isEmpty());
    }

    @DisplayName("получение ссылок в непустой таблицу")
    @Test
    @Transactional
    @Rollback
    void givenNonEmptyChatLinksRepository_whenGetLinks_thenReceiveFullList() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());
        Link l2 = new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now());

        linkRepository.add(l1);
        linkRepository.add(l2);

        var allLinks = linkRepository.findAll();

        Assertions.assertEquals(2, allLinks.size());
    }

    @DisplayName("Добавление новой ссылки добавляет ссылку")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenAddLink_thenAdds() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());

        var res = linkRepository.add(l1);

        var allLinks = linkRepository.findAll();

        Assertions.assertEquals(1, allLinks.size());
        Assertions.assertEquals(l1.getDescription(), res.getDescription());
    }

    @DisplayName("Добавление существующей ссылки кидает ошибку")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenAddExistedLink_thenException() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());

        linkRepository.add(l1);
        assertThrows(RuntimeException.class, () -> linkRepository.add(l1));

    }

    @DisplayName("Удаление существующей ссылки добавляет ссылку")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenRemoveExistedLink_thenRemoves() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());
/*
        userLinksRepository.add(l1);

        var res = userLinksRepository.remove(l1);
        Assertions.assertTrue(userLinksRepository.findAll().isEmpty());
        Assertions.assertNotNull(res);*/

    }

    @DisplayName("Удаление существующей ссылки добавляет ссылку")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenRemoveNonExistedLink_thenNull() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());

        var res = linkRepository.remove(l1);
        Assertions.assertTrue(linkRepository.findAll().isEmpty());
        assertNull(res);

    }

    @DisplayName("Exists существующей ссылки")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenExistsExistedLink_thenTrue() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());
        l1 = linkRepository.add(l1);

        var res = linkRepository.exists(l1);

        assertTrue(res);

    }

    @DisplayName("Exists не существующей ссылки")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenExistsNonExistedLink_thenFalse() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());

        var res = linkRepository.exists(l1);

        assertFalse(res);

    }

    @DisplayName("добавление пользователю ссылки")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenAddUserLinks_then1() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());
        Chat c = new Chat(1L, OffsetDateTime.now());
        l1 = linkRepository.add(l1);
        chatRepository.add(c);

        var res = linkRepository.addChatLink(c, l1);
        assertEquals(1, res);

    }

    @DisplayName("добавление пользователю ссылки, которой нет")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenAddUserNonExistingLink_thenException() {
        Link l1 = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());
        Chat c = new Chat(1L, OffsetDateTime.now());
        chatRepository.add(c);

        assertThrows(RuntimeException.class, () -> linkRepository.addChatLink(c, l1));
    }

    @DisplayName("добавление пользователю, которого нет ссылки")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenAddNonExistingUserLink_thenException() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());

        assertThrows(RuntimeException.class, () -> linkRepository.addChatLink(c, l1));
    }

    @DisplayName("нахождение всех ссылок пользователя")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindAllUserLinksWithLinks_thenList() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());
        chatRepository.add(c);

        linkRepository.addChatLink(c, l1);
        linkRepository.addChatLink(c, l2);

        var res = linkRepository.findAllChatLinks(c);
        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(link -> link.getId().equals(l1.getId())));
        assertTrue(res.stream().anyMatch(link -> link.getId().equals(l2.getId())));
    }

    @DisplayName("нахождение всех ссылок пользователя без ссылок")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindAllUserLinksWithoutLinks_thenList() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());
        chatRepository.add(c);

        var res = linkRepository.findAllChatLinks(c);
        assertTrue(res.isEmpty());
    }

    @DisplayName("нахождение всех пользователей по ссылке без пользователей")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindUsersByLinkWithoutUsers_thenEmptyList() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());
        Chat c2 = new Chat(2L, OffsetDateTime.now());
        chatRepository.add(c);
        chatRepository.add(c2);

        linkRepository.addChatLink(c, l1);
        linkRepository.addChatLink(c, l2);
        linkRepository.addChatLink(c2, l1);

        var res = linkRepository.findChatsByLink(l2);
        assertEquals(1, res.size());
        var elenemt = res.get(0);
        assertEquals(c.getId(), elenemt.getId());

    }

    @DisplayName("уборка ссылки у пользователя, у которого нет этой ссылки")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenremoveUserLinkWithoutLinks_then0() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());
        Chat c2 = new Chat(2L, OffsetDateTime.now());
        chatRepository.add(c);
        chatRepository.add(c2);

        linkRepository.addChatLink(c, l1);
        linkRepository.addChatLink(c, l2);
        linkRepository.addChatLink(c2, l1);

        var res = linkRepository.removeChatLink(c2, l2);
        assertEquals(0, res);

    }

    @DisplayName("уборка ссылки у пользователя,")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenremoveUserLink_then0() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());
        Chat c2 = new Chat(2L, OffsetDateTime.now());
        chatRepository.add(c);
        chatRepository.add(c2);

        linkRepository.addChatLink(c, l1);
        linkRepository.addChatLink(c, l2);
        linkRepository.addChatLink(c2, l1);

        var res = linkRepository.removeChatLink(c2, l1);
        assertEquals(1, res);

    }

    @DisplayName("нахождение всех пользователей по ссылке")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindUsersByLinkWithUsers_thenEmptyList() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));
        Chat c = new Chat(1L, OffsetDateTime.now());
        chatRepository.add(c);

        var res = linkRepository.findChatsByLink(l1);
        assertTrue(res.isEmpty());
    }

    @DisplayName("Нахождение Link по ссылке")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindByExistingUrl_thenLink() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));

        var res = linkRepository.findByUrl("https://youtube.com");
        assertNotNull(res);
        assertEquals(l2.getUrl(), res.getUrl());
        assertEquals(l2.getId(), res.getId());

    }

    @DisplayName("Нахождение Link по ссылке, при отсутсствии элемента возвращает null")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindByNonExistingUrl_thenNull() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://youtube.com"), "", OffsetDateTime.now()));

        var res = linkRepository.findByUrl("https://github.com");
        assertNull(res);

    }

    @DisplayName("Нахождение Link по ссылке, при отсутсствии элемента возвращает null")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenUpdate_thenUrlUpdates() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.MIN));

        OffsetDateTime offsetDateTime = OffsetDateTime.now();

        l1.setUpdatedAt(offsetDateTime);

        linkRepository.updateLink(l1);

        var res = linkRepository.findByUrl(l1.getUrl().toString());

        offsetDateTime = offsetDateTime.minusNanos(offsetDateTime.getNano());
        var time =res.getUpdatedAt().minusNanos(res.getUpdatedAt().getNano());
        Assertions.assertTrue(offsetDateTime.isEqual(time));
    }

    @DisplayName("Нахождение Link по ссылке, при отсутсствии элемента возвращает null")
    @Test
    @Transactional
    @Rollback
    void givenLinkRepository_whenFindMoreThenFifeMinutesLaterUpdated_thenListOfLinks() {
        Link l1 = linkRepository.add(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.MIN));
        Link l2 = linkRepository.add(new Link(1L, URI.create("https://stack.com"), "", OffsetDateTime.now().minusMinutes(10)));
        Link l3 = linkRepository.add(new Link(1L, URI.create("https://stackoverflow.com"), "", OffsetDateTime.now().minusMinutes(4)));

        var res = linkRepository.findMoreThenFifeMinutesLaterUpdated();

        Assertions.assertNotNull(res);
        Assertions.assertEquals(2,res.size());
    }

}
