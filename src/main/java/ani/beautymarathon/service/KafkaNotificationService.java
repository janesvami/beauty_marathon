package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
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
    public void sendSimpleEmail(NotificationRequest notificationRequest) {
        kafkaProducer.sendNotification(notificationRequest);
    }
}
