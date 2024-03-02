package edu.java.entity.repository;

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

    @DisplayName("Получение всех ссылок пользователя, у которого нет ссылок")
    @Test
    void givenUserLinksRepository_whenGetLinksWithUserWithoutLinks_thenReceiveEmptyList() {

        URI vk = URI.create("https://vk.com");
        URI youtube = URI.create("https://youtube.com");

        userLinksRepository.add(1L, vk);
        userLinksRepository.add(1L, youtube);

        userLinksRepository.add(3L, vk);

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

        userLinksRepository.add(1L, vk);
        userLinksRepository.add(1L, youtube);

        userLinksRepository.add(3L, vk);

        var allLinks = userLinksRepository.getLinks(1L);

        Assertions.assertTrue(allLinks.containsAll(firstUserLinksTest));
    }
}
