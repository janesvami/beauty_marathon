package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Profile("docker")
public class KafkaNotificationProducer {
    private static final String TOPIC = "notifications-topic";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public KafkaNotificationProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Synchronous sending with waiting for result
     * @param request Request to send notification
     * @throws RuntimeException if sending throws an error
     */
    public void sendNotificationSync(NotificationRequest request) {
        sendNotificationSyncWithKey(UUID.randomUUID().toString(), request);
    }

    /**
     * Synchronous sending with custom key
     * @param key Message key
     * @param request Request to send notification
     * @throws RuntimeException If sending error
     */
    public void sendNotificationSyncWithKey(
            String key, NotificationRequest request) {

        if (!canSendNotification(request)) {
            //TODO: create a special exception class
            throw new RuntimeException("Can't send notification");
        }

        try {
            log.info("🔄 Sending notification synchronously. Key: '{}'", key);

            Message<NotificationRequest> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    .setHeader("message-id", UUID.randomUUID().toString())
                    .setHeader("created-at", LocalDateTime.now().format(TIMESTAMP_FORMATTER))
                    .setHeader("message-type", "EMAIL")
                    .build();

            CompletableFuture<SendResult<String, NotificationRequest>> future =
                    kafkaTemplate.send(message);

            SendResult<String, NotificationRequest> result = future.get();

            log.info("✅ Sync notification sent. Partition: {}, Offset: {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

            log.info("Result of sending notification: {}", result);
        } catch (Exception e) {
            log.error("❌ Failed to send notification synchronously. Key: '{}'", key, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the message can be sent
     */
    public boolean canSendNotification(NotificationRequest request) {
        if (request == null) {
            log.warn("❌ Notification request is null");
            return false;
        }

        if (request.addressesTo() == null || request.addressesTo().isEmpty()) {
            log.warn("❌ No recipients specified");
            return false;
        }

        if (request.subject() == null || request.subject().trim().isEmpty()) {
            log.warn("⚠️ Notification subject is empty");
        }

        if (request.text() == null || request.text().trim().isEmpty()) {
            log.warn("⚠️ Notification text is empty");
        }

        return true;
    }
}