package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("docker")
public class KafkaNotificationProducer extends AbstractKafkaProducer<NotificationRequest> {

    private static final String TOPIC = "notifications-topic";

    protected KafkaNotificationProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getKafkaTopic() {
        return TOPIC;
    }
}