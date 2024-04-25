package edu.java.services.kafka;

import edu.java.configuration.ApplicationConfig;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.services.LinkUpdateSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;

@Log4j2
@RequiredArgsConstructor
public class ScrapperQueueProducer implements LinkUpdateSenderService {

    private final ApplicationConfig config;
    private final KafkaTemplate<Long, LinkUpdateRequest> template;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        log.info("отправил запрос в kafka");
        template.send(config.kafkaInfo().topic().name(), linkUpdateRequest.id(), linkUpdateRequest);
    }
}
