package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaNotificationProducer kafkaProducer;

    /**
     * Sends a simple email notification
     */
    public SendResult<String, NotificationRequest> sendSimpleEmail(NotificationRequest notificationRequest) throws Exception {
        return kafkaProducer.sendNotificationSync(notificationRequest);
    }
}
