package edu.java.entity.repository;

import edu.java.entity.Chat;
import edu.java.entity.Link;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LinkRepository {

    private static final Long MINUTES_TILL_BECOMING_OLD = 5L;

    private static final String SQL_INSERT_LINK =
        "insert into link (url, description, updated_at) values (?,?,?) RETURNING id, updated_at, url, description";
    private static final String SQL_ALL_LINKS =
        "select l.id, l.url, l.description, l.updated_at from link l";
    private static final String SQL_ALL_CHAT_LINKS =
        "select l.id, l.url, l.description, l.updated_at from link l join public.telegram_chat_link tcl on l.id = tcl.link_id where tcl.chat_id = ?";


    private static final String SQL_ADD_CHAT_LINK =
        "insert into telegram_chat_link (chat_id, link_id) values (?, ?)";
    private static final String SQL_REMOVE_LINK =
        "delete from link l where l.id = ? returning id, url, description , updated_at";
    private static final String SQL_REMOVE_CHAT_LINK =
        "delete from telegram_chat_link tlc where tlc.chat_id = ? and tlc.link_id = ?";

    private static final String SQL_EXISTS =
        "select exists(select 1 from link l where l.id = ?)";

    private static final String SQL_FIND_BY_URL =
        "select l.id, l.url, l.description, l.updated_at from link l where l.url = ?";

    private static final String SQL_UPDATE_LINK =
        "update link set updated_at = ?  where id = ?";
    private static final String SQL_FIND_FAR_UPDATED =
        "select l.id, l.url, l.description, l.updated_at from link l where l.updated_at < ?";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Link> linkMapper;

    public LinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        linkMapper = new BeanPropertyRowMapper<>(Link.class);
    }

    /**
     * добавляет ссылку в базу
     */

    public Link add(Link link) {
        return jdbcTemplate.queryForObject(
            SQL_INSERT_LINK,
            linkMapper,
            link.getUrl().toString(),
            link.getDescription(),
            link.getUpdatedAt()
        );
    }

    /**
     * добавляет пользователю ссылку
     */

    public boolean addChatLink(Chat chat, Link link) {
        var res = jdbcTemplate.update(
            SQL_ADD_CHAT_LINK,
            chat.getId(),
            link.getId()
        );
        return res == 1;
    }

    /**
     * дает все имеющиеся ссылки
     */

    public List<Link> findAll() {
        return jdbcTemplate.query(
            SQL_ALL_LINKS,
            linkMapper
        );
    }

    /**
     * находит все ссылки пользователя
     */

    public List<Link> findAllChatLinks(Chat chat) {
        return jdbcTemplate.query(
            SQL_ALL_CHAT_LINKS,
            linkMapper,
            chat.getId()
        );
    }



    /**
     * удаляет ссылку из базы
     */

    public Link remove(Link link) {
        var a = jdbcTemplate.query(
            SQL_REMOVE_LINK,
            linkMapper,
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

    public boolean removeChatLink(Chat chat, Link link) {
        var res = jdbcTemplate.update(
            SQL_REMOVE_CHAT_LINK,
            chat.getId(),
            link.getId()
        );
        return res == 1;
    }

    /**
     * существует ли ссылка
     */

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

    public Link findByUrl(String link) {
        var a = jdbcTemplate.query(
            SQL_FIND_BY_URL,
            linkMapper,
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

    public boolean updateLink(Link link) {
        var res =jdbcTemplate.update(
            SQL_UPDATE_LINK,
            link.getUpdatedAt(),
            link.getId()
        );
        return res == 1;
    }

    /**
     * нахождение давно обновленных
     */

    public List<Link> findMoreThenFifeMinutesLaterUpdated() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().minusMinutes(MINUTES_TILL_BECOMING_OLD);

        return jdbcTemplate.query(
            SQL_FIND_FAR_UPDATED,
            linkMapper,
            offsetDateTime
        );
    }

}
