package edu.java.configuration;

import edu.java.entity.repository.ChatRepository;
import edu.java.entity.repository.LinkRepository;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import edu.java.services.jdbc.JdbcChatService;
import edu.java.services.jdbc.JdbcLinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public LinkService jdbcLinkService(ChatRepository chatRepository, LinkRepository linkRepository) {
        return new JdbcLinkService(chatRepository, linkRepository);
    }

    @Bean
    public ChatService jdbcChatService(ChatRepository chatRepository) {
        return new JdbcChatService(chatRepository);
    }
}
