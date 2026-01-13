package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import ani.beautymarathon.exception.KafkaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("docker")
@RequiredArgsConstructor
public class KafkaNotificationService implements NotificationService {

    private final KafkaNotificationProducer kafkaProducer;

    /**
     * Sends a simple email notification
     */
    public void sendEmail(NotificationRequest notificationRequest) {

        if (!canSendNotification(notificationRequest)) {
            throw new KafkaException("Can't send notification");
        }

        final String TOPIC = kafkaProducer.getKafkaTopic();
        kafkaProducer.sendNotification(notificationRequest, TOPIC);
    }

    /**
     * Checks if the message can be sent
     */
    private boolean canSendNotification(NotificationRequest request) {
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
