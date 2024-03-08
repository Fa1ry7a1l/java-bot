package edu.java.scrapper;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.Assert.assertTrue;

@Testcontainers
@Log4j2
public class TableCreatingTest extends IntegrationTest {

    @Test
    @DisplayName("Telegram chat link table существует")
    public void testTelegramChatLinkTableExists() {
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );
             Statement statement = connection.createStatement()) {
            ResultSet tableTelegramChatExists = statement.executeQuery(
                "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'telegram_chat_link');");
            tableTelegramChatExists.next();
            assertTrue(tableTelegramChatExists.getBoolean(1));
        } catch (SQLException e) {
            log.error(e);
            assertTrue("sql ошибка", false);
        }
    }

    @Test
    @DisplayName("Link table существует")
    public void testLinkTableExists() {
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        ); Statement statement = connection.createStatement()) {

            ResultSet tableLinkExists = statement.executeQuery(
                "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'link');");
            tableLinkExists.next();
            assertTrue(tableLinkExists.getBoolean(1));

        } catch (SQLException e) {
            log.error(e);
            assertTrue("sql ошибка", false);
        }
    }

    @Test
    @DisplayName("Telegram chat table существует")
    public void testTelegramChatTableExists() {
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );
             Statement statement = connection.createStatement()) {
            ResultSet tableTelegramChatExists = statement.executeQuery(
                "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'telegram_chat');");
            tableTelegramChatExists.next();
            assertTrue(tableTelegramChatExists.getBoolean(1));
        } catch (SQLException e) {
            log.error(e);
            assertTrue("sql ошибка", false);
        }
    }

}
