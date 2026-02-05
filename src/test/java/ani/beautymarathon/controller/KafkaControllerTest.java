package ani.beautymarathon.controller;

import ani.beautymarathon.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KafkaController.class)
@AutoConfigureMockMvc(addFilters = false)
class KafkaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void sendNotification_shouldReturn200() throws Exception {
        mockMvc.perform(get("/send"))
                .andExpect(status().isOk());
    }

    @Test
    void sendNotification_shouldCallService() throws Exception {
        mockMvc.perform(get("/send"))
                .andExpect(status().isOk());

        verify(notificationService).sendEmail(any());
    }
}
