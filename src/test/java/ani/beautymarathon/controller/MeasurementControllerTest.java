package ani.beautymarathon.controller;

import ani.beautymarathon.entity.ClosedState;
import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.UserMeasurement;
import ani.beautymarathon.entity.WkMeasurement;
import ani.beautymarathon.service.MeasurementService;
import ani.beautymarathon.view.filter.register.MoMeasurementFilter;
import ani.beautymarathon.view.filter.register.RegisterUserFilter;
import ani.beautymarathon.view.filter.register.RegisterWeekFilter;
import ani.beautymarathon.view.filter.register.UserMeasurementFilter;
import ani.beautymarathon.view.measurement.CreateUserMeasurementView;
import ani.beautymarathon.view.measurement.CreateWkMeasurementView;
import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.measurement.GetUserMeasurementView;
import ani.beautymarathon.view.measurement.UpdateUserMeasurementView;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static ani.beautymarathon.MeasurementsTestHelper.createTestMoMeasurement;
import static ani.beautymarathon.MeasurementsTestHelper.createTestMoMeasurementView;
import static ani.beautymarathon.MeasurementsTestHelper.createTestUserMeasurement;
import static ani.beautymarathon.MeasurementsTestHelper.createTestUserMeasurementView;
import static ani.beautymarathon.MeasurementsTestHelper.createTestWkMeasurement;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MeasurementController.class)
@AutoConfigureMockMvc
class MeasurementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeasurementService measurementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createWkMeasurement_WhenValidValues_ThenSuccess() throws Exception {
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        CreateWkMeasurementView createWkMeasurementView = new CreateWkMeasurementView(
                wkMeasurement.getMeasurementDate(),
                wkMeasurement.getCommentary()
        );

        when(measurementService.createWkMeasurement(any())).thenReturn(wkMeasurement);

        mockMvc.perform(post("/measurements/wk/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWkMeasurementView)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(wkMeasurement.getId()))
                .andExpect(jsonPath("$.moMeasurement.id").value(wkMeasurement.getMoMeasurement().getId()))
                .andExpect(jsonPath("$.closedState").value(wkMeasurement.getClosedState().toString()))
                .andExpect(jsonPath("$.measurementDate").value(wkMeasurement.getMeasurementDate().toString()))
                .andExpect(jsonPath("$.commentary").value(wkMeasurement.getCommentary()));
    }

    @Test
    void createWkMeasurement_WhenInvalidValues_ThenBadRequest() throws Exception {
        String badJson = """
                {
                    "measurementDate": null,
                    "commentary": "23"
                }
                """;

        mockMvc.perform(post("/measurements/wk/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("measurementDate")));
    }

    @Test
    void createUserMeasurement_WhenValidValues_ThenSuccess() throws Exception {
        UserMeasurement userMeasurement = createTestUserMeasurement();
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                userMeasurement.getWkMeasurement().getId(),
                userMeasurement.getUser().getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(measurementService.createUserMeasurement(any())).thenReturn(userMeasurement);

        mockMvc.perform(post("/measurements/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserMeasurementView)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userMeasurement.getId()))
                .andExpect(jsonPath("$.user.id").value(userMeasurement.getUser().getId()))
                .andExpect(jsonPath("$.wkMeasurement.id").value(userMeasurement.getWkMeasurement().getId()))
                .andExpect(jsonPath("$.weight").value(userMeasurement.getWeight()))
                .andExpect(jsonPath("$.weightPoint").value(userMeasurement.getWeightPoint()))
                .andExpect(jsonPath("$.sleepPoint").value(userMeasurement.getSleepPoint()))
                .andExpect(jsonPath("$.waterPoint").value(userMeasurement.getWaterPoint()))
                .andExpect(jsonPath("$.stepPoint").value(userMeasurement.getStepPoint()))
                .andExpect(jsonPath("$.diaryPoint").value(userMeasurement.getDiaryPoint()))
                .andExpect(jsonPath("$.alcoholFreePoints").value(userMeasurement.getAlcoholFreePoint()))
                .andExpect(jsonPath("$.commentary").value(userMeasurement.getCommentary()));
    }

    @Test
    void createUserMeasurement_WhenInvalidValues_ThenBadRequest() throws Exception {
        String badJson = """
                {
                    "wkMeasurementId": null,
                    "userId": null,
                    "weight": 350,
                    "weightPoint": 13,
                    "sleepPoint": 32,
                    "stepPoint": 23,
                    "diaryPoint": 32,
                    "alcoholFreePoints": 65,
                    "commentary": "test"
                }
                """;

        mockMvc.perform(post("/measurements/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("wkMeasurementId")))
                .andExpect(jsonPath("$.message", containsString("userId")))
                .andExpect(jsonPath("$.message", containsString("weight")))
                .andExpect(jsonPath("$.message", containsString("weightPoint")))
                .andExpect(jsonPath("$.message", containsString("sleepPoint")))
                .andExpect(jsonPath("$.message", containsString("waterPoint")))
                .andExpect(jsonPath("$.message", containsString("stepPoint")))
                .andExpect(jsonPath("$.message", containsString("diaryPoint")))
                .andExpect(jsonPath("$.message", containsString("alcoholFreePoint")));
    }

    @Test
    void createUserMeasurement_WhenUserNotFound_Then404() throws Exception {
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                1L,
                99999999L,
                new BigDecimal("55"),
                2,
                5,
                5,
                5,
                5,
                5,
                "test"
        );

        when(measurementService.createUserMeasurement(any()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(post("/measurements/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserMeasurementView)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void createUserMeasurement_WhenWkNotFound_Then404() throws Exception {
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                1L,
                99999999L,
                new BigDecimal("55"),
                2,
                5,
                5,
                5,
                5,
                5,
                "test"
        );

        when(measurementService.createUserMeasurement(any()))
                .thenThrow(new EntityNotFoundException("WkMeasurement not found"));

        mockMvc.perform(post("/measurements/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserMeasurementView)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("WkMeasurement not found"));
    }

    @Test
    void getCascadeOfAllMeasurements_WhenFound_ThenSuccess() throws Exception {
        MoMeasurement moMeasurement1 = createTestMoMeasurement();
        MoMeasurement moMeasurement2 = createTestMoMeasurement();
        MoMeasurement moMeasurement3 = createTestMoMeasurement();
        List<MoMeasurement> moMeasurements = Arrays.asList(moMeasurement1, moMeasurement2, moMeasurement3);

        Pageable pageable = PageRequest.of(0, 5);
        Page<MoMeasurement> mockPage = new PageImpl<>(moMeasurements, pageable, moMeasurements.size());
        MoMeasurementFilter filter = new MoMeasurementFilter(ClosedState.OPEN, 2025, 9);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filterJson = objectMapper.writeValueAsString(filter);

        GetMoMeasurementView moMeasurementView1 = createTestMoMeasurementView(moMeasurement1);
        GetMoMeasurementView moMeasurementView2 = createTestMoMeasurementView(moMeasurement2);
        GetMoMeasurementView moMeasurementView3 = createTestMoMeasurementView(moMeasurement3);
        ClosedState closedState = moMeasurementView3.closedState();

        when(measurementService.getCascadeOfAllMeasurements(any(), any())).thenReturn(mockPage);

        mockMvc.perform(post("/measurements/mo/all")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].monthNumber").value(moMeasurementView1.monthNumber()))
                .andExpect(jsonPath("$.content[1].year").value(moMeasurementView2.year()))
                .andExpect(jsonPath("$.content[2].closedState").value(closedState.toString()));
    }

    @Test
    void getAllMeasurements_WhenFound_ThenSuccess() throws Exception {
        UserMeasurement userMeasurement1 = createTestUserMeasurement();
        UserMeasurement userMeasurement2 = createTestUserMeasurement();
        UserMeasurement userMeasurement3 = createTestUserMeasurement();
        List<UserMeasurement> userMeasurements = Arrays.asList(userMeasurement1, userMeasurement2, userMeasurement3);

        Pageable pageable = PageRequest.of(0, 5);
        Page<UserMeasurement> mockPage = new PageImpl<>(userMeasurements, pageable, userMeasurements.size());
        RegisterUserFilter registerUserFilter = new RegisterUserFilter("Fesha", DeletedState.NOT_DELETED);
        RegisterWeekFilter registerWeekFilter = new RegisterWeekFilter(
                2025,
                9,
                ClosedState.OPEN,
                ClosedState.OPEN
        );
        UserMeasurementFilter filter = new UserMeasurementFilter(registerUserFilter, registerWeekFilter);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filterJson = objectMapper.writeValueAsString(filter);

        GetUserMeasurementView userMeasurementView1 = createTestUserMeasurementView(userMeasurement1);
        GetUserMeasurementView userMeasurementView2 = createTestUserMeasurementView(userMeasurement2);
        GetUserMeasurementView userMeasurementView3 = createTestUserMeasurementView(userMeasurement3);

        when(measurementService.getAllUserMeasurements(any(), any())).thenReturn(mockPage);

        mockMvc.perform(post("/measurements/user/all")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].weight").value(userMeasurementView1.weight()))
                .andExpect(jsonPath("$.content[1].sleepPoint").value(userMeasurementView2.sleepPoint()))
                .andExpect(jsonPath("$.content[2].weightPoint").value(userMeasurementView3.weightPoint()));
    }

    @Test
    void updateWkStatus_WhenValidValues_ThenSuccess() throws Exception {
        WkMeasurement wkMeasurement = createTestWkMeasurement();

        when(measurementService.updateWkStatus(wkMeasurement.getId(), wkMeasurement.getClosedState())).thenReturn(wkMeasurement);

        mockMvc.perform(put("/measurements/wk/status/" + wkMeasurement.getId())
                        .param("newState", wkMeasurement.getClosedState().name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wkMeasurement.getId()))
                .andExpect(jsonPath("$.closedState").value(wkMeasurement.getClosedState().toString()));
    }

    @Test
    void updateWkStatus_WhenWkNotFound_Then404() throws Exception {
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        String errorMessage = "Not Found";

        when(measurementService.updateWkStatus(wkMeasurement.getId(), wkMeasurement.getClosedState()))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/measurements/wk/status/" + wkMeasurement.getId())
                        .param("newState", wkMeasurement.getClosedState().name()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void updateMoStatus_WhenValidValues_ThenSuccess() throws Exception {
        MoMeasurement moMeasurement = createTestMoMeasurement();

        when(measurementService.updateMoStatus(moMeasurement.getId(), moMeasurement.getClosedState()))
                .thenReturn(moMeasurement);

        mockMvc.perform(put("/measurements/mo/status/" + moMeasurement.getId())
                        .param("newState", moMeasurement.getClosedState().name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(moMeasurement.getId()))
                .andExpect(jsonPath("$.closedState").value(moMeasurement.getClosedState().toString()));
    }

    @Test
    void updateMoStatus_WhenMoNotFound_Then404() throws Exception {
        MoMeasurement moMeasurement = createTestMoMeasurement();
        String errorMessage = "Not Found";

        when(measurementService.updateMoStatus(moMeasurement.getId(), moMeasurement.getClosedState()))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/measurements/mo/status/" + moMeasurement.getId())
                        .param("newState", moMeasurement.getClosedState().name()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void updateUserMeasurementView_WhenValidValues_ThenSuccess() throws Exception {
        UserMeasurement userMeasurement = createTestUserMeasurement();
        UpdateUserMeasurementView updateUserMeasurementView = new UpdateUserMeasurementView(
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(measurementService.updateMeasurement(userMeasurement.getId(), updateUserMeasurementView))
                .thenReturn(userMeasurement);

        mockMvc.perform(put("/measurements/user/update/" + userMeasurement.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserMeasurementView)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(userMeasurement.getWeight()))
                .andExpect(jsonPath("$.weightPoint").value(userMeasurement.getWeightPoint()))
                .andExpect(jsonPath("$.sleepPoint").value(userMeasurement.getSleepPoint()))
                .andExpect(jsonPath("$.waterPoint").value(userMeasurement.getWaterPoint()))
                .andExpect(jsonPath("$.stepPoint").value(userMeasurement.getStepPoint()))
                .andExpect(jsonPath("$.diaryPoint").value(userMeasurement.getDiaryPoint()))
                .andExpect(jsonPath("$.alcoholFreePoints").value(userMeasurement.getAlcoholFreePoint()))
                .andExpect(jsonPath("$.commentary").value(userMeasurement.getCommentary()));
    }

    @Test
    void updateUserMeasurementView_WhenInvalidValues_ThenBadRequest() throws Exception {
        long id = 1;
        String badJson = """
                {
                    "weight": 350,
                    "weightPoint": 13,
                    "sleepPoint": 32,
                    "waterPoint": 23,
                    "stepPoint": 23,
                    "diaryPoint": 32,
                    "alcoholFreePoints": 65,
                    "commentary": "test"
                }
                """;

        mockMvc.perform(put("/measurements/user/update/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("weight")))
                .andExpect(jsonPath("$.message", containsString("weightPoint")))
                .andExpect(jsonPath("$.message", containsString("sleepPoint")))
                .andExpect(jsonPath("$.message", containsString("stepPoint")))
                .andExpect(jsonPath("$.message", containsString("diaryPoint")))
                .andExpect(jsonPath("$.message", containsString("alcoholFreePoints")));
    }

    @Test
    void updateUserMeasurementView_WhenNotFound_Then404() throws Exception {
        UserMeasurement userMeasurement = createTestUserMeasurement();
        UpdateUserMeasurementView updateUserMeasurementView = new UpdateUserMeasurementView(
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );
        String errorMessage = "User measurement with id " + userMeasurement.getId() + " not found";

        when(measurementService.updateMeasurement(userMeasurement.getId(), updateUserMeasurementView))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/measurements/user/update/" + userMeasurement.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserMeasurementView)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}