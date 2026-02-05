package ani.beautymarathon.service;

import ani.beautymarathon.entity.NotificationRequest;
import ani.beautymarathon.exception.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    private AbstractKafkaProducer<?> producer;

    @BeforeEach
    void setUp() {
        producer = new TestKafkaProducer(kafkaTemplate);
    }

    @Test
    void sendNotification_shouldSendKafkaMessageSuccessfully() {
        NotificationRequest request = new NotificationRequest(
                List.of("test@mail.com"),
                "Test subject",
                "Hello from test"
        );
        String topic = "test-topic";

        ArgumentCaptor<Message<NotificationRequest>> captor =
                ArgumentCaptor.forClass(Message.class);

        producer.sendNotification(request, topic);

        verify(kafkaTemplate, times(1)).send(captor.capture());

        Message<NotificationRequest> message = captor.getValue();

        assertThat(message.getPayload()).isEqualTo(request);
        assertThat(message.getHeaders().get(KafkaHeaders.TOPIC))
                .isEqualTo(topic);

        assertThat(message.getHeaders()).containsKeys(
                KafkaHeaders.KEY,
                "message-id",
                "created-at",
                "message-type"
        );

        assertThat(message.getHeaders().get("message-type"))
                .isEqualTo("EMAIL");
    }

    @Test
    void sendNotification_shouldThrowKafkaExceptionWhenSendFails() {
        NotificationRequest request = new NotificationRequest(
                List.of("fail@mail.com"),
                "Fail subject",
                "Kafka error"
        );

        doThrow(new RuntimeException("Kafka is down"))
                .when(kafkaTemplate)
                .send(any(Message.class));

        assertThatThrownBy(() ->
                producer.sendNotification(request, "test-topic")
        )
                .isInstanceOf(KafkaException.class)
                .hasMessageContaining("Kafka is down");
    }

    /**
     * Test implementation of abstract class
     */
    static class TestKafkaProducer extends AbstractKafkaProducer<KafkaMessage> {

        protected TestKafkaProducer(KafkaTemplate<String, NotificationRequest> kafkaTemplate) {
            super(kafkaTemplate);
        }

        @Override
        protected String getKafkaTopic() {
            return "test-topic";
        }
    }
}
