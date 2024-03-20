package edu.java.entity.repository;

import edu.java.entity.Chat;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class ChatRepository {

    private static final String SQL_REMOVE_USER = "delete from telegram_chat tc where tc.id=? returning *";
    private static final String SQL_INSERT_USER =
        "insert into telegram_chat (id, registered_at) values (?,?) returning *";
    private static final String SQL_SELECT_ALL = "select * from telegram_chat";
    private static final String SQL_EXISTS = "select exists(select * from telegram_chat tc where tc.id = ?) ";
    private static final String SQL_FIND = "select * from telegram_chat tc where tc.id = ? ";

    private final JdbcTemplate jdbcTemplate;

    /**
     * находит всех пользователей
     */
    @Transactional
    public List<Chat> findAll() {
        return jdbcTemplate.query(
            SQL_SELECT_ALL,
            new BeanPropertyRowMapper<>(Chat.class)
        );
    }

    /**
     * добавляет пользователя
     */
    @Transactional
    public Chat add(Chat chat) {
        var a = jdbcTemplate.queryForObject(
            SQL_INSERT_USER,
            new BeanPropertyRowMapper<>(Chat.class),
            chat.getId(),
            chat.getRegisteredAt()
        );
        return a;
    }

    /**
     * удаляет пользователя
     */
    @Transactional
    public Chat remove(Chat chat) {
        var a = jdbcTemplate.query(
            SQL_REMOVE_USER,
            new BeanPropertyRowMapper<>(Chat.class),
            chat.getId()
        );
        if (a.isEmpty()) {
            return null;
        }
        return a.getFirst();
    }

    /**
     * существует ли чат
     */
    @Transactional
    public boolean exists(Chat chat) {
        var a = jdbcTemplate.queryForObject(
            SQL_EXISTS,
            Boolean.class,
            chat.getId()
        );
        return a;
    }

    /**
     * существует ли чат
     */
    @Transactional
    public Chat find(Long chatId) {
        var a = jdbcTemplate.query(
            SQL_FIND,
            new BeanPropertyRowMapper<>(Chat.class),
            chatId
        );
        if (a.isEmpty()) {
            return null;
        }
        return a.getFirst();
    }
}
