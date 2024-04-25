package edu.java.configuration;

import edu.java.clients.BotClient;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.services.LinkUpdateSenderService;
import edu.java.services.http.ScrapperHttpUpdateService;
import edu.java.services.kafka.ScrapperQueueProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Log4j2
public class UpdatesConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
    public LinkUpdateSenderService kafkaUpdatesSender(
        ApplicationConfig config,
        KafkaTemplate<Long, LinkUpdateRequest> template
    ) {
        log.info("updates messages uses kafka");
        return new ScrapperQueueProducer(config, template);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "false")
    public LinkUpdateSenderService httpUpdatesService(BotClient client) {
        log.info("updates messages uses http");
        return new ScrapperHttpUpdateService(client);
    }

}
