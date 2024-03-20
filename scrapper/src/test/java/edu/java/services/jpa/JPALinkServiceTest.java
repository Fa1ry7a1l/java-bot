package edu.java.services.jpa;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import edu.java.entity.repository.jpa.JPAChatRepository;
import edu.java.entity.repository.jpa.JPALinkRepository;
import edu.java.exceptions.DeletingNotExistingUrlException;
import edu.java.exceptions.UserNotFoundException;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class JPALinkServiceTest {
    private LinkService linkService;

    @Autowired
    private JPAChatRepository chatRepository;

    @Autowired
    private JPALinkRepository linkRepository;

    @BeforeEach
    public void beforeEach() {
        linkService = new JPALinkService(chatRepository, linkRepository);
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenGetAllLinks_thenReceiveListLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Link l2 = new Link(2L, URI.create("https://stackoverflow.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());
        chat.setLinks(Set.of(l1, l2));

        chatRepository.save(chat);

        var res = linkService.getAllLinks(chat.getId());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals(2, res.links().size());
        Assertions.assertTrue(res.links().stream().anyMatch(linkResponse -> linkResponse.url().equals(l1.getUrl())));
        Assertions.assertTrue(res.links().stream().anyMatch(linkResponse -> linkResponse.url().equals(l2.getUrl())));

    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenGetAllLinksForUnknownChat_thenException() {

        Assertions.assertThrows(UserNotFoundException.class, () -> linkService.getAllLinks(1L));
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenAddLinkForUnknownChat_thenException() {

        Assertions.assertThrows(
            UserNotFoundException.class,
            () -> linkService.addLink(1L, new AddLinkRequest(URI.create("https://github.com")))
        );
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenAddLink_thenReceiveListLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());
        chat.setLinks(new HashSet<>());

        chatRepository.save(chat);

        var res = linkService.addLink(chat.getId(), new AddLinkRequest(l1.getUrl()));

        Assertions.assertNotNull(res);
        Assertions.assertEquals(l1.getUrl(), res.url());
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenAddKnownLink_thenReceiveListLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        chatRepository.save(chat);
        linkRepository.save(l1);

        var res = linkService.addLink(chat.getId(), new AddLinkRequest(l1.getUrl()));

        Assertions.assertNotNull(res);
        Assertions.assertEquals(l1.getId(), res.id());
        Assertions.assertEquals(l1.getUrl(), res.url());
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenRemoveLinkNonExistedChat_thenException() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        linkRepository.save(l1);

        Assertions.assertThrows(
            UserNotFoundException.class,
            () -> linkService.removeLink(chat.getId(), new RemoveLinkRequest(l1.getUrl()))
        );

    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenRemoveUnknownLink_thenException() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        chatRepository.save(chat);

        Assertions.assertThrows(
            DeletingNotExistingUrlException.class,
            () -> linkService.removeLink(chat.getId(), new RemoveLinkRequest(l1.getUrl()))
        );

    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenRemoveLink_thenLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        chat.getLinks().add(l1);
        chatRepository.save(chat);

        var res = linkService.removeLink(chat.getId(), new RemoveLinkRequest(l1.getUrl()));

        Assertions.assertEquals(l1.getId(), res.id());
        Assertions.assertEquals(l1.getUrl(), res.url());
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenFindMoreThenFifeMinutesLaterUpdated_thenCallRepository() {
        Link l1 = new Link(0L, URI.create("https://github.com"), "", OffsetDateTime.now().minusMinutes(10));
        Link l2 = new Link(0L, URI.create("https://youtube.com"), "", OffsetDateTime.now());
        Link l3 = new Link(0L, URI.create("https://vk.com"), "", OffsetDateTime.now().minusMinutes(3));

        linkRepository.save(l1);
        linkRepository.save(l2);
        linkRepository.save(l3);

        var res = linkService.findUpdateableLinks();
        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals("https://github.com", res.getFirst().getUrl().toString());
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenUpdateList_thenCallRepository() {

        Chat c = new Chat(1L, OffsetDateTime.now());

        var link = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now().minusDays(150));
        link.getChats().add(c);
        linkRepository.save(link);

        OffsetDateTime time = OffsetDateTime.now();

        link.setUpdatedAt(time);
        linkService.updateLink(link);
        var res = linkRepository.findById(link.getId());

        System.out.println(res.get().getChats().stream().findFirst());
    }

    @Test
    @Transactional
    @Rollback
    public void givenRepository_whenChatsByLink_thenCallRepository() {
        Link l = new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        l.getChats().add(chat);
        linkRepository.save(l);

        var chats = linkService.chatsByLink(l);

        Assertions.assertTrue(chats.containsAll(l.getChats()));
        Assertions.assertEquals(l.getChats().size(), chats.size());
    }
}
