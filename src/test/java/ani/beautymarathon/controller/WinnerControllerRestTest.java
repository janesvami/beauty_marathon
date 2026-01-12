package ani.beautymarathon.controller;

import ani.beautymarathon.entity.ClosedState;
import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.service.WinnerService;
import ani.beautymarathon.view.filter.register.WinnerFilter;
import ani.beautymarathon.view.filter.register.WinnerMonthFilter;
import ani.beautymarathon.view.filter.register.WinnerUserFilter;
import ani.beautymarathon.view.winner.GetWinnerView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static ani.beautymarathon.MeasurementsTestHelper.createTestMoMeasurement;
import static ani.beautymarathon.MeasurementsTestHelper.createTestMoMeasurementView;
import static ani.beautymarathon.UserTestHelper.createTestUser;
import static ani.beautymarathon.UserTestHelper.createTestUserView;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WinnerController.class)
@AutoConfigureMockMvc
class WinnerControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WinnerService winnerService;


    @Test
    void getById_WhenFound_ThenSuccess() throws Exception {
        long id = 1;
        LocalDate today = LocalDate.now();
        MoMeasurement testMoMeasurement = createTestMoMeasurement();
        User testUser = createTestUser();
        Winner winner = new Winner();
        winner.setId(id);
        winner.setMoMeasurement(testMoMeasurement);
        winner.setUser(testUser);
        winner.setAveragePoint(45.2);
        winner.setCreationDate(today);

        when(winnerService.getById(id)).thenReturn(winner);

        mockMvc.perform(get("/winners/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.moMeasurement.id").value(testMoMeasurement.getId()))
                .andExpect(jsonPath("$.moMeasurement.year").value(testMoMeasurement.getYear()))
                .andExpect(jsonPath("$.moMeasurement.closedState").value(testMoMeasurement.getClosedState().toString()))
                .andExpect(jsonPath("$.moMeasurement.monthNumber").value(testMoMeasurement.getMonthNumber()))
                .andExpect(jsonPath("$.user.id").value(testUser.getId()))
                .andExpect(jsonPath("$.user.name").value(testUser.getName()))
                .andExpect(jsonPath("$.averagePoint").value(45.2))
                .andExpect(jsonPath("$.creationDate").value(today.toString()));
    }

    @Test
    void getById_WhenNotFound_Then404() throws Exception {
        long id = 1;
        String errorMessage = "Not Found";
        when(winnerService.getById(id)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(get("/winners/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getAllWinners_WhenFound_ThenSuccess() throws Exception {
        long id = 1;
        Double averagePoint = 45.2;
        LocalDate today = LocalDate.now();
        MoMeasurement testMoMeasurement = createTestMoMeasurement();
        User testUser = createTestUser();
        Winner winnerFirst = new Winner();
        winnerFirst.setId(id);
        winnerFirst.setMoMeasurement(testMoMeasurement);
        winnerFirst.setUser(testUser);
        winnerFirst.setAveragePoint(averagePoint);
        winnerFirst.setCreationDate(today);

        long id2 = 2;
        Double averagePoint2 = 38.5;
        LocalDate today2 = LocalDate.now().minusDays(1);
        MoMeasurement testMoMeasurement2 = createTestMoMeasurement();
        User testUser2 = createTestUser();
        Winner winnerSecond = new Winner();
        winnerSecond.setId(id2);
        winnerSecond.setMoMeasurement(testMoMeasurement2);
        winnerSecond.setUser(testUser2);
        winnerSecond.setAveragePoint(averagePoint2);
        winnerSecond.setCreationDate(today2);

        Pageable pageable = PageRequest.of(0, 5);
        List<Winner> expectedWinners = Arrays.asList(winnerFirst, winnerSecond);
        Page<Winner> mockPage = new PageImpl<>(expectedWinners, pageable, 2);

        when(winnerService.getAllWinners(any(), any())).thenReturn(mockPage);

        WinnerMonthFilter monthFilter = new WinnerMonthFilter(2025, 9, ClosedState.CLOSED);
        WinnerUserFilter userFilter = new WinnerUserFilter("Fesha", DeletedState.NOT_DELETED);
        WinnerFilter filter = new WinnerFilter(monthFilter, userFilter);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filterJson = objectMapper.writeValueAsString(filter);

        GetWinnerView firstWinnerView = new GetWinnerView(
                id,
                createTestMoMeasurementView(testMoMeasurement),
                createTestUserView(testUser),
                averagePoint,
                today
        );

        GetWinnerView secondWinnerView = new GetWinnerView(
                id2,
                createTestMoMeasurementView(testMoMeasurement2),
                createTestUserView(testUser2),
                averagePoint2,
                today2
        );

        mockMvc.perform(post("/winners/all")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .content(filterJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].id").value(firstWinnerView.id()))
                .andExpect(jsonPath("$.content[0].averagePoint").value(firstWinnerView.averagePoint()))
                .andExpect(jsonPath("$.content[0].creationDate").value(firstWinnerView.creationDate().toString()))
                .andExpect(jsonPath("$.content[1].id").value(secondWinnerView.id()))
                .andExpect(jsonPath("$.content[1].averagePoint").value(secondWinnerView.averagePoint()))
                .andExpect(jsonPath("$.content[1].creationDate").value(secondWinnerView.creationDate().toString()));
    }
}