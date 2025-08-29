package uy.com.inventory.inventory_service.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    DeadLetterPublishingRecoverer dlpr(KafkaTemplate<String, Object> template) {
        return new DeadLetterPublishingRecoverer(template, (r, e) -> new org.apache.kafka.common.TopicPartition("inventory-commands.dlq", r.partition()));
    }

    @Bean
    DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer dlpr) {
        var backoff = new org.springframework.util.backoff.FixedBackOff(200L, 3L);
        var handler = new DefaultErrorHandler(dlpr, backoff);
        handler.addNotRetryableExceptions(IllegalArgumentException.class);
        return handler;
    }
}
