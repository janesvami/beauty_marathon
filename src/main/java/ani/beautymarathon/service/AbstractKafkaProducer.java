package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import ani.beautymarathon.exception.KafkaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public abstract class AbstractKafkaProducer<M extends KafkaMessage> {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    protected AbstractKafkaProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sending without waiting for result
     * @param request Request to send notification
     * @throws RuntimeException if sending throws an error
     */
    public void sendNotification(NotificationRequest request, String topic) {
        final String key = UUID.randomUUID().toString();

        try {
            log.info("🔄 Sending notification. Key: '{}'", key);

            Message<NotificationRequest> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("message-id", UUID.randomUUID().toString())
                    .setHeader("created-at", LocalDateTime.now().format(TIMESTAMP_FORMATTER))
                    .setHeader("message-type", "EMAIL")
                    .build();

            kafkaTemplate.send(message);

        } catch (Exception e) {
            log.error("❌ Failed to send notification. Key: '{}'", key, e);
            throw new KafkaException(e.getMessage());
        }
    }

    protected abstract String getKafkaTopic();
}
