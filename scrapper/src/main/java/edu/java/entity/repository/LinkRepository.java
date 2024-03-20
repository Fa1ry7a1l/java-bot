package edu.java.entity.repository;

import edu.java.entity.Chat;
import edu.java.entity.Link;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Repository
public class LinkRepository {

    private static final Long MINUTES_TILL_BECOMING_OLD = 5L;

    private static final String SQL_INSERT_LINK =
        "insert into link (url, description, updated_at) values (?,?,?) RETURNING *";
    private static final String SQL_ALL_LINKS =
        "select * from link";
    private static final String SQL_ALL_CHAT_LINKS =
        "select * from link l join public.telegram_chat_link tcl on l.id = tcl.link_id where tcl.chat_id = ?";
    private static final String SQL_ALL_CHATS_BY_LINK =
        "select tc.* from link "
            + "l join public.telegram_chat_link tcl on l.id = tcl.link_id "
            + "join public.telegram_chat tc on tc.id = tcl.chat_id "
            + "where l.id = ?";

    private static final String SQL_ADD_CHAT_LINK =
        "insert into telegram_chat_link (chat_id, link_id) values (?, ?)";
    private static final String SQL_REMOVE_LINK =
        "delete from link l where l.id = ? returning *";
    private static final String SQL_REMOVE_CHAT_LINK =
        "delete from telegram_chat_link tlc where tlc.chat_id = ? and tlc.link_id = ?";

    private static final String SQL_EXISTS =
        "select exists(select * from link l where l.id = ?)";

    private static final String SQL_FIND_BY_URL =
        "select * from link l where l.url = ?";

    private static final String SQL_UPDATE_LINK =
        "update link set updated_at = ?  where id = ?";
    private static final String SQL_FIND_FAR_UPDATED =
        "select * from link l where l.updated_at < ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * добавляет ссылку в базу
     */
    @Transactional
    public Link add(Link link) {
        return jdbcTemplate.queryForObject(
            SQL_INSERT_LINK,
            new BeanPropertyRowMapper<>(Link.class),
            link.getUrl().toString(),
            link.getDescription(),
            link.getUpdatedAt()
        );
    }

    /**
     * добавляет пользователю ссылку
     */
    @Transactional
    public int addChatLink(Chat chat, Link link) {
        return jdbcTemplate.update(
            SQL_ADD_CHAT_LINK,
            chat.getId(),
            link.getId()
        );
    }

    /**
     * дает все имеющиеся ссылки
     */
    @Transactional
    public List<Link> findAll() {
        return jdbcTemplate.query(
            SQL_ALL_LINKS,
            new BeanPropertyRowMapper<>(Link.class)
        );
    }

    /**
     * находит все ссылки пользователя
     */
    @Transactional
    public List<Link> findAllChatLinks(Chat chat) {
        return jdbcTemplate.query(
            SQL_ALL_CHAT_LINKS,
            new BeanPropertyRowMapper<>(Link.class), chat.getId()
        );
    }

    /**
     * находит всех пользователей, которые мониторят ссылку
     */
    @Transactional
    public List<Chat> findChatsByLink(Link link) {
        return jdbcTemplate.query(
            SQL_ALL_CHATS_BY_LINK,
            new BeanPropertyRowMapper<>(Chat.class), link.getId()
        );
    }

    /**
     * удаляет ссылку из базы
     */
    @Transactional
    public Link remove(Link link) {
        var a = jdbcTemplate.query(
            SQL_REMOVE_LINK,
            new BeanPropertyRowMapper<>(Link.class),
            link.getId()
        );
        if (a.isEmpty()) {
            return null;
        }
        return a.getFirst();
    }

    /**
     * убирает у пользователя ссылку
     */
    @Transactional
    public int removeChatLink(Chat chat, Link link) {
        return jdbcTemplate.update(
            SQL_REMOVE_CHAT_LINK,
            chat.getId(),
            link.getId()
        );
    }

    /**
     * существует ли ссылка
     */
    @Transactional
    public boolean exists(Link link) {
        return jdbcTemplate.queryForObject(
            SQL_EXISTS,
            Boolean.class,
            link.getId()
        );
    }

    /**
     * получение ссылки по url
     */
    @Transactional
    public Link findByUrl(String link) {
        var a = jdbcTemplate.query(
            SQL_FIND_BY_URL,
            new BeanPropertyRowMapper<>(Link.class),
            link
        );

        if (a.isEmpty()) {
            return null;
        }
        return a.getFirst();
    }

    /**
     * обновление времени проверки у ссылки
     */
    @Transactional
    public void updateLink(Link link) {
        jdbcTemplate.update(
            SQL_UPDATE_LINK,
            link.getUpdatedAt(),
            link.getId()
        );
    }

    /**
     * нахождение давно обновленных
     */
    @Transactional
    public List<Link> findMoreThenFifeMinutesLaterUpdated() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().minusMinutes(MINUTES_TILL_BECOMING_OLD);

        return jdbcTemplate.query(
            SQL_FIND_FAR_UPDATED,
            new BeanPropertyRowMapper<>(Link.class),
            offsetDateTime
        );
    }

}
