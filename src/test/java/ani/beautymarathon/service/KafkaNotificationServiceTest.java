package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import ani.beautymarathon.exception.KafkaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("docker")
@ExtendWith(MockitoExtension.class)
class KafkaNotificationServiceTest {

    @Mock
    private KafkaNotificationProducer kafkaProducer;

    @InjectMocks
    private KafkaNotificationService service;

    @Test
    void sendEmail_shouldSendEmailSuccessfully() {
        NotificationRequest request = validRequest();
        when(kafkaProducer.getKafkaTopic()).thenReturn("email-topic");
        service.sendEmail(request);
        verify(kafkaProducer).sendNotification(request, "email-topic");
    }

    @Test
    void sendEmail_shouldThrowExceptionWhenRequestIsNull() {
        assertThatThrownBy(() -> service.sendEmail(null))
                .isInstanceOf(KafkaException.class)
                .hasMessageContaining("Can't send notification");

        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void sendEmail_shouldThrowExceptionWhenAddressesEmpty() {
        NotificationRequest request = new NotificationRequest(
                List.of(),
                "Subject",
                "Text"
        );

        assertThatThrownBy(() -> service.sendEmail(request))
                .isInstanceOf(KafkaException.class);

        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void sendEmail_shouldSendEvenIfSubjectIsEmpty() {
        NotificationRequest request = new NotificationRequest(
                List.of("test@mail.com"),
                "",
                "Body text"
        );

        when(kafkaProducer.getKafkaTopic()).thenReturn("email-topic");
        service.sendEmail(request);

        verify(kafkaProducer).sendNotification(request, "email-topic");
    }

    @Test
    void sendEmail_shouldSendEvenIfTextIsEmpty() {
        NotificationRequest request = new NotificationRequest(
                List.of("test@mail.com"),
                "Subject",
                ""
        );

        when(kafkaProducer.getKafkaTopic()).thenReturn("email-topic");
        service.sendEmail(request);

        verify(kafkaProducer).sendNotification(request, "email-topic");
    }

    private NotificationRequest validRequest() {
        return new NotificationRequest(
                List.of("user@mail.com"),
                "Test subject",
                "Hello"
        );
    }
}

