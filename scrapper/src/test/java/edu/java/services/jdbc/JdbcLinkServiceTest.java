package edu.java.services.jdbc;

import edu.java.dtos.AddLinkRequest;
import edu.java.dtos.RemoveLinkRequest;
import edu.java.entity.Chat;
import edu.java.entity.Link;
import edu.java.entity.repository.ChatRepository;
import edu.java.entity.repository.LinkRepository;
import edu.java.exceptions.DeletingNotExistingUrlException;
import edu.java.exceptions.ReaddingLinkException;
import edu.java.exceptions.UserNotFoundException;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcLinkServiceTest {

    private LinkService linkService;

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private LinkRepository linkRepository;

    @BeforeEach
    public void beforeEach() {
        linkService = new JdbcLinkService(chatRepository, linkRepository);
    }

    @Test
    public void givenRepository_whenGetAllLinks_thenReceiveListLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Link l2 = new Link(2L, URI.create("https://stackoverflow.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findAllChatLinks(any())).thenReturn(List.of(l1, l2));

        var res = linkService.getAllLinks(chat.getId());

        Assertions.assertNotNull(res);
        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals(2, res.links().size());
        Assertions.assertTrue(res.links().stream().anyMatch(linkResponse -> linkResponse.url().equals(l1.getUrl())));
        Assertions.assertTrue(res.links().stream().anyMatch(linkResponse -> linkResponse.url().equals(l2.getUrl())));

    }

    @Test
    public void givenRepository_whenGetAllLinksForUnknownChat_thenException() {

        when(chatRepository.find(any())).thenReturn(null);
        Assertions.assertThrows(UserNotFoundException.class, () -> linkService.getAllLinks(1L));
    }

    @Test
    public void givenRepository_whenAddLinkForUnknownChat_thenException() {

        when(chatRepository.find(any())).thenReturn(null);
        Assertions.assertThrows(
            UserNotFoundException.class,
            () -> linkService.addLink(1L, new AddLinkRequest(URI.create("https://github.com")))
        );
    }

    @Test
    public void givenRepository_whenAddLink_thenReceiveListLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findByUrl(any())).thenReturn(null);
        when(linkRepository.add(any())).thenReturn(l1);
        when(linkRepository.addChatLink(chat, l1)).thenReturn(1);

        var res = linkService.addLink(chat.getId(), new AddLinkRequest(l1.getUrl()));

        verify(chatRepository, times(1)).find(any());
        verifyNoMoreInteractions(chatRepository);

        verify(linkRepository, times(1)).findByUrl(any());
        verify(linkRepository, times(1)).add(any());
        verify(linkRepository, times(1)).addChatLink(any(), any());
        verifyNoMoreInteractions(linkRepository);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(l1.getId(), res.id());
        Assertions.assertEquals(l1.getUrl(), res.url());
    }

    @Test
    public void givenRepository_whenAddKnownLink_thenReceiveListLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findByUrl(any())).thenReturn(l1);
        when(linkRepository.addChatLink(chat, l1)).thenReturn(1);

        var res = linkService.addLink(chat.getId(), new AddLinkRequest(l1.getUrl()));

        verify(chatRepository, times(1)).find(any());
        verifyNoMoreInteractions(chatRepository);

        verify(linkRepository, times(1)).findByUrl(any());
        verify(linkRepository, never()).add(any());
        verify(linkRepository, times(1)).addChatLink(any(), any());
        verifyNoMoreInteractions(linkRepository);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(l1.getId(), res.id());
        Assertions.assertEquals(l1.getUrl(), res.url());
    }

    @Test
    public void givenRepository_whenAddKnownLinkWithImpossibleException_thenException() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findByUrl(any())).thenReturn(l1);
        when(linkRepository.addChatLink(chat, l1)).thenReturn(3);

        Assertions.assertThrows(
            ReaddingLinkException.class,
            () -> linkService.addLink(chat.getId(), new AddLinkRequest(l1.getUrl()))
        );

        verify(chatRepository, times(1)).find(any());
        verifyNoMoreInteractions(chatRepository);

        verify(linkRepository, times(1)).findByUrl(any());
        verify(linkRepository, never()).add(any());
        verify(linkRepository, times(1)).addChatLink(any(), any());
        verifyNoMoreInteractions(linkRepository);
    }

    @Test
    public void givenRepository_whenAddLinkWithManyUpdates_thenReceiveException() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findByUrl(any())).thenReturn(null);
        when(linkRepository.add(any())).thenReturn(l1);
        when(linkRepository.addChatLink(chat, l1)).thenReturn(3);

        Assertions.assertThrows(
            ReaddingLinkException.class,
            () -> linkService.addLink(chat.getId(), new AddLinkRequest(l1.getUrl()))
        );

    }

    @Test
    public void givenRepository_whenRemoveLinkNonExistedChat_thenException() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(null);

        Assertions.assertThrows(
            UserNotFoundException.class,
            () -> linkService.removeLink(chat.getId(), new RemoveLinkRequest(l1.getUrl()))
        );

    }

    @Test
    public void givenRepository_whenRemoveUnknownLink_thenException() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findByUrl(any())).thenReturn(null);

        Assertions.assertThrows(
            DeletingNotExistingUrlException.class,
            () -> linkService.removeLink(chat.getId(), new RemoveLinkRequest(l1.getUrl()))
        );

    }

    @Test
    public void givenRepository_whenRemoveLink_thenLinkResponse() {
        Link l1 = new Link(1L, URI.create("https://github.com"), "", OffsetDateTime.now());
        Chat chat = new Chat(1L, OffsetDateTime.now());

        when(chatRepository.find(any())).thenReturn(chat);
        when(linkRepository.findByUrl(any())).thenReturn(l1);
        when(linkRepository.removeChatLink(chat, l1)).thenReturn(1);

        var res = linkService.removeLink(chat.getId(), new RemoveLinkRequest(l1.getUrl()));

        verify(chatRepository, times(1)).find(any());
        verifyNoMoreInteractions(chatRepository);

        verify(linkRepository, times(1)).findByUrl(any());
        verify(linkRepository, times(1)).removeChatLink(any(), any());
        verifyNoMoreInteractions(linkRepository);

        Assertions.assertEquals(l1.getId(), res.id());
        Assertions.assertEquals(l1.getUrl(), res.url());
    }

    @Test
    public void givenRepository_whenFindMoreThenFifeMinutesLaterUpdated_thenCallRepository() {
        linkService.findUpdateableLinks();
        verify(linkRepository, times(1)).findMoreThenFifeMinutesLaterUpdated();
        verifyNoMoreInteractions(linkRepository);
    }

    @Test
    public void givenRepository_whenUpdateList_thenCallRepository() {
        linkService.updateLink(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        verify(linkRepository, times(1)).updateLink(any());
        verifyNoMoreInteractions(linkRepository);
    }
    @Test
    public void givenRepository_whenchatsByLink_thenCallRepository() {
        linkService.chatsByLink(new Link(1L, URI.create("https://vk.com"), "", OffsetDateTime.now()));
        verify(linkRepository, times(1)).findChatsByLink(any());
        verifyNoMoreInteractions(linkRepository);
    }
}
