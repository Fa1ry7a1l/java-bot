package edu.java.bot.entity.repository;

import edu.java.bot.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRepositoryTest {

    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository = new UserRepository();
    }

    @DisplayName("getUser отсутствующего пользователя")
    @Test
    void givenUserRepository_whenGetsNotExistingUser_thenReceiveEmpty() {

        var res = userRepository.getUser(1L);

        Assertions.assertTrue(res.isEmpty());
    }

    @DisplayName("getUser существующего")
    @Test
    void givenUserRepository_whenGetsExistingUser_thenReceiveUser() {
        userRepository.put(1L, new User(1));
        userRepository.put(3L, new User(3));

        var res = userRepository.getUser(1L);

        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(1L, res.get().getTelegramId());
    }

    @DisplayName("put существующего")
    @Test
    void givenUserRepository_whenPutsExistingUser_thenUserChanges() {
        userRepository.put(1L, new User(1));
        userRepository.put(3L, new User(3));

        userRepository.put(1L, new User(1444L));

        var res = userRepository.getUser(1L);

        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(1444L, res.get().getTelegramId());
    }

    @DisplayName("put не существующего")
    @Test
    void givenUserRepository_whenPutsNonExistingUser_thenUserChanges() {
        userRepository.put(1L, new User(1));
        userRepository.put(3L, new User(3));

        userRepository.put(1444L, new User(1444L));

        var res = userRepository.getUser(1444L);

        Assertions.assertTrue(res.isPresent());
        Assertions.assertEquals(1444L, res.get().getTelegramId());
    }
}
