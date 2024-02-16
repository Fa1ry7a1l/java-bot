package edu.java.bot.entity.repository;

import java.net.URI;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserLinksRepositoryTest {

    UserLinksRepository userLinksRepository;

    @BeforeEach
    public void setUp() {
        userLinksRepository = new UserLinksRepository();
    }

    @DisplayName("все ссылки при пустом репозитории")
    @Test
    void givenEmptyUserLinksRepository_whenTriesReceiveAllLinks_thenReceiveEmptyMap() {
        var allLinks = userLinksRepository.getLinks();

        Assertions.assertTrue(allLinks.isEmpty());
    }

    @DisplayName("все ссылки при непустом репозитории")
    @Test
    void givenUserLinksRepository_whenTriesReceiveAllLinks_thenReceiveNonEmptyMap() {
        URI vk = URI.create("https://vk.com");
        URI youtube = URI.create("https://youtube.com");
        URI git = URI.create("https://github.com");
        ArrayList<URI> firstUserLinksTest = new ArrayList<>();
        firstUserLinksTest.add(vk);
        firstUserLinksTest.add(youtube);

        ArrayList<URI> secondUserLinksTest = new ArrayList<>();
        secondUserLinksTest.add(git);

        userLinksRepository.addLink(1L, vk);
        userLinksRepository.addLink(3L, git);
        userLinksRepository.addLink(1L, youtube);

        var allLinks = userLinksRepository.getLinks();

        Assertions.assertFalse(allLinks.isEmpty());
        Assertions.assertEquals(2, allLinks.size());
        Assertions.assertTrue(allLinks.get(1L).containsAll(firstUserLinksTest));
        Assertions.assertTrue(allLinks.get(3L).containsAll(secondUserLinksTest));
    }

    @DisplayName("добавление в пользователя который уже есть")

    @Test
    void givenUserLinksRepository_whenAddToUserWithLinks_thenReceiveAllLinks() {

        URI vk = URI.create("https://vk.com");
        URI youtube = URI.create("https://youtube.com");
        URI git = URI.create("https://github.com");
        ArrayList<URI> firstUserLinksTest = new ArrayList<>();
        firstUserLinksTest.add(vk);
        firstUserLinksTest.add(youtube);

        ArrayList<URI> secondUserLinksTest = new ArrayList<>();
        secondUserLinksTest.add(git);
        secondUserLinksTest.add(vk);

        userLinksRepository.addLink(1L, vk);
        userLinksRepository.addLink(3L, git);
        userLinksRepository.addLink(1L, youtube);

        userLinksRepository.addLink(3L, vk);

        var allLinks = userLinksRepository.getLinks();

        Assertions.assertFalse(allLinks.isEmpty());
        Assertions.assertEquals(2, allLinks.size());
        Assertions.assertTrue(allLinks.get(1L).containsAll(firstUserLinksTest));
        Assertions.assertTrue(allLinks.get(3L).containsAll(secondUserLinksTest));
    }

    @DisplayName("добавление в пользователя у которого нет ссылок")
    @Test
    void givenUserLinksRepository_whenAddToUserWithoutLinks_thenReceiveAllLinks() {

        URI vk = URI.create("https://vk.com");
        URI youtube = URI.create("https://youtube.com");
        ArrayList<URI> firstUserLinksTest = new ArrayList<>();
        firstUserLinksTest.add(vk);
        firstUserLinksTest.add(youtube);

        ArrayList<URI> secondUserLinksTest = new ArrayList<>();
        secondUserLinksTest.add(vk);

        userLinksRepository.addLink(1L, vk);
        userLinksRepository.addLink(1L, youtube);

        userLinksRepository.addLink(3L, vk);

        var allLinks = userLinksRepository.getLinks();

        Assertions.assertFalse(allLinks.isEmpty());
        Assertions.assertEquals(2, allLinks.size());
        Assertions.assertTrue(allLinks.get(1L).containsAll(firstUserLinksTest));
        Assertions.assertTrue(allLinks.get(3L).containsAll(secondUserLinksTest));
    }

    @DisplayName("Получение всех ссылок пользователя, у которого нет ссылок")
    @Test
    void givenUserLinksRepository_whenGetLinksWithUserWithoutLinks_thenReceiveEmptyList() {

        URI vk = URI.create("https://vk.com");
        URI youtube = URI.create("https://youtube.com");

        userLinksRepository.addLink(1L, vk);
        userLinksRepository.addLink(1L, youtube);

        userLinksRepository.addLink(3L, vk);

        var allLinks = userLinksRepository.getLinks(4L);

        Assertions.assertTrue(allLinks.isEmpty());
    }

    @DisplayName("Получение всех ссылок пользователя, у которого нет ссылок")
    @Test
    void givenUserLinksRepository_whenGetLinksWithUserWithLinks_thenReceiveListWithAllUsersLinks() {

        URI vk = URI.create("https://vk.com");
        URI youtube = URI.create("https://youtube.com");
        ArrayList<URI> firstUserLinksTest = new ArrayList<>();
        firstUserLinksTest.add(vk);
        firstUserLinksTest.add(youtube);

        userLinksRepository.addLink(1L, vk);
        userLinksRepository.addLink(1L, youtube);

        userLinksRepository.addLink(3L, vk);

        var allLinks = userLinksRepository.getLinks(1L);

        Assertions.assertTrue(allLinks.containsAll(firstUserLinksTest));
    }
}
