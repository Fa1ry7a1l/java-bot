package edu.java.entity.repository;

import edu.java.entity.Chat;
import edu.java.entity.Link;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {

    private static final String SQL_REMOVE_USER =
        "delete from telegram_chat tc where tc.id=? returning tc.id, tc.registered_at";
    private static final String SQL_INSERT_USER =
        "insert into telegram_chat (id, registered_at) values (?,?) returning  id, registered_at";
    private static final String SQL_SELECT_ALL = "select  tc.id, tc.registered_at from telegram_chat tc";
    private static final String SQL_EXISTS = "select exists(select 1 from telegram_chat tc where tc.id = ?) ";
    private static final String SQL_FIND = "select  tc.id, tc.registered_at from telegram_chat tc where tc.id = ? ";

    private static final String SQL_ALL_CHATS_BY_LINK =
        "select tc.id, tc.registered_at from link "
            + "l join public.telegram_chat_link tcl on l.id = tcl.link_id "
            + "join public.telegram_chat tc on tc.id = tcl.chat_id "
            + "where l.id = ?";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Chat> chatMapper;

    public ChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        chatMapper = new BeanPropertyRowMapper<>(Chat.class);
    }

    /**
     * находит всех пользователей
     */
    public List<Chat> findAll() {
        return jdbcTemplate.query(
            SQL_SELECT_ALL,
            chatMapper
        );
    }

    /**
     * добавляет пользователя
     */
    public Chat add(Chat chat) {
        var a = jdbcTemplate.queryForObject(
            SQL_INSERT_USER,
            chatMapper,
            chat.getId(),
            chat.getRegisteredAt()
        );
        return a;
    }

    /**
     * удаляет пользователя
     */
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
    public Chat find(Long chatId) {
        var a = jdbcTemplate.query(
            SQL_FIND,
            chatMapper,
            chatId
        );
        if (a.isEmpty()) {
            return null;
        }
        return a.getFirst();
    }

    /**
     * находит всех пользователей, которые мониторят ссылку
     */

    public List<Chat> findChatsByLink(Link link) {
        return jdbcTemplate.query(
            SQL_ALL_CHATS_BY_LINK,
            chatMapper,
            link.getId()
        );
    }
}
