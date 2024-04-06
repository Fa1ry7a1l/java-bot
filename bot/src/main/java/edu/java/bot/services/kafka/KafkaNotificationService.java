package edu.java.bot.services.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.services.NotificationService;
import edu.java.dtos.LinkUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class KafkaNotificationService {

    private final ApplicationConfig config;
    private final NotificationService notificationService;
    private final KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate;

    @KafkaListener(groupId = "scrapper.updates.listeners",
                   topics = "${app.kafka-info.topic.name}",
                   containerFactory = "linkUpdateRequestConcurrentKafkaListenerContainerFactory")
    public void listen(LinkUpdateRequest update) {
        try {
            notificationService.sendUpdateNotification(update);
        } catch (RuntimeException runtimeException) {
            log.info("Ошибка при обработке сообщения");
            log.error(runtimeException);
            kafkaTemplate.send(config.kafkaInfo().topic() + "_dlq", update.id(), update);
        }

    }
}
