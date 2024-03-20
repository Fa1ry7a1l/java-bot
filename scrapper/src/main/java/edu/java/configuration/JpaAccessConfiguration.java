package edu.java.configuration;

import edu.java.entity.repository.jpa.JPAChatRepository;
import edu.java.entity.repository.jpa.JPALinkRepository;
import edu.java.services.jpa.JPAChatService;
import edu.java.services.jpa.JPALinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public JPALinkService jpaLinkService(JPAChatRepository chatRepository, JPALinkRepository linkRepository) {
        return new JPALinkService(chatRepository, linkRepository);
    }

    @Bean
    public JPAChatService jpaChatService(JPAChatRepository chatRepository) {
        return new JPAChatService(chatRepository);
    }
}
