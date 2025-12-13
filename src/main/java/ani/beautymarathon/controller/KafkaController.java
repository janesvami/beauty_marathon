package ani.beautymarathon.controller;

import ani.beautymarathon.entity.NotificationRequest;
import ani.beautymarathon.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KafkaController {
    private final NotificationService notificationService;

    public KafkaController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/send")
    public void sendNotification() {
        NotificationRequest notificationRequest = new NotificationRequest(
                List.of("iegorzaika@gmail.com"),
                "subject",
                "text"
        );
        notificationService.sendSimpleEmail(notificationRequest);
    }
}
