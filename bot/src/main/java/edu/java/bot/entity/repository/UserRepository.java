package edu.java.bot.entity.repository;

import edu.java.bot.entity.User;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final HashMap<Long, User> usersMap = new HashMap<>();

    public Optional<User> getUser(Long id) {
        if (usersMap.containsKey(id)) {
            return Optional.of(usersMap.get(id));
        }
        return Optional.empty();
    }

    public void put(Long id, User user) {
        usersMap.put(id, user);
    }
}
