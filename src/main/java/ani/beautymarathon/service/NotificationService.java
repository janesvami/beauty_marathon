package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;

public interface NotificationService {
    void sendSimpleEmail(NotificationRequest notificationRequest);
}