package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("no-docker")
@RequiredArgsConstructor
public class NoDockerNotificationService implements NotificationService {

    /**
     * Stub method for no docker environment
     */
    public void sendSimpleEmail(NotificationRequest notificationRequest) {
        log.info("The message will not be send. The method is stub");
    }
}
