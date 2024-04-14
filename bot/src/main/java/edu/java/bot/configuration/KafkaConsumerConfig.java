package edu.java.bot.configuration;

import edu.java.dtos.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
@Log4j2
public class KafkaConsumerConfig {

    @Bean
    public NewTopic topicDLQ(ApplicationConfig applicationConfig) {

        var topic = applicationConfig.kafkaInfo().topic();
        log.info("partitions {}", topic.partitions());

        return TopicBuilder.name(topic.dlqName())
            .partitions(topic.partitions())
            .replicas(topic.replicas())
            .build();
    }

    @Bean
    public NewTopic topic(ApplicationConfig applicationConfig) {

        var topic = applicationConfig.kafkaInfo().topic();

        return TopicBuilder.name(topic.name())
            .partitions(topic.partitions())
            .replicas(topic.replicas())
            .build();
    }

    @Bean
    public ConsumerFactory<Long, LinkUpdateRequest> consumerFactory(ApplicationConfig config) {
        return new DefaultKafkaConsumerFactory<>(receiverProps(config.kafkaInfo()),new LongDeserializer(),new JsonDeserializer<>(LinkUpdateRequest.class));
    }

    private Map<String, Object> receiverProps(ApplicationConfig.KafkaInfo config) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers());
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class);
        return props;
    }

    @Bean
    public ProducerFactory<Long, LinkUpdateRequest> producerFactory(ApplicationConfig config) {
        return new DefaultKafkaProducerFactory<>(senderProps(config.kafkaInfo()));
    }

    private Map<String, Object> senderProps(ApplicationConfig.KafkaInfo config) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers());
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateRequest>
    linkUpdateRequestConcurrentKafkaListenerContainerFactory(ConsumerFactory<Long, LinkUpdateRequest> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate(
        ProducerFactory<Long, LinkUpdateRequest> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
