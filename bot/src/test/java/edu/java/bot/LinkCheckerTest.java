package edu.java.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LinkCheckerTest {
    static LinkChecker linkChecker = new LinkChecker();

    @DisplayName("Тест правильной ссылки")
    @Test
    void givenLinkTracker_whenTriesValidateCorrectLinkWith_thenReceivesURIWIthHost() {

        var uri = linkChecker.tryValidate("https://vk.com");

        Assertions.assertEquals("https://vk.com", uri.toString());
        Assertions.assertEquals("vk.com", uri.getHost());
    }

    @DisplayName("Тест ссылки без протокола")
    @Test
    void givenLinkTracker_whenTriesValidateLinkWithoutProtocol_thenReceivesURIWIthHost() {

        var uri = linkChecker.tryValidate("vk.com");

        Assertions.assertEquals("vk.com", uri.toString());
        Assertions.assertNull(uri.getHost());
    }

    @DisplayName("Тест некоррестной ссылки")
    @Test
    void givenLinkTracker_whenTriesValidateIncorrectLink_thenReceivesURIWIthHost() {

        var uri = linkChecker.tryValidate("vk.com?*&^");

        Assertions.assertNull(uri);
    }
}
