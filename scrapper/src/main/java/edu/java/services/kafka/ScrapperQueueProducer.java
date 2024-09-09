package edu.java.services.kafka;

import edu.java.configuration.ApplicationConfig;
import edu.java.dtos.LinkUpdateRequest;
import edu.java.services.LinkUpdateSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements LinkUpdateSenderService {

    private final ApplicationConfig config;
    private final KafkaTemplate<Long, LinkUpdateRequest> template;

    @Override
    public void send(LinkUpdateRequest linkUpdateRequest) {
        template.send(config.kafkaInfo().topic().name(), linkUpdateRequest.id(), linkUpdateRequest);
    }
}
