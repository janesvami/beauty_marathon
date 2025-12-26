package ani.beautymarathon.controller;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.service.UserService;
import ani.beautymarathon.view.user.CreateUserView;
import ani.beautymarathon.view.user.UpdateUserView;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static ani.beautymarathon.MeasurementsTestHelper.createTestUser;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerRestTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createUser_WhenValidValues_ThenSuccess() throws Exception {
        User user = createTestUser();
        CreateUserView createUserView = new CreateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );

        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserView)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.startWeight").value(user.getStartWeight()))
                .andExpect(jsonPath("$.targetWeight").value(user.getTargetWeight()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void createUser_WhenInvalidValues_ThenBadRequest() throws Exception {
        String badJson = """
                {
                    "name": 13,
                    "startWeight": 25,
                    "targetWeight": 250,
                    "email": "ouch"
                }
                """;

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("startWeight")))
                .andExpect(jsonPath("$.message", containsString("targetWeight")))
                .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    void findAll_WhenFound_ThenSuccess() throws Exception {
        User user = createTestUser();
        User user2 = createTestUser();
        User user3 = createTestUser();

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        users.add(user3);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[2].name").value(user3.getName()));
    }

    @Test
    void getById_WhenNotFound_Then404() throws Exception {
        long id = 1;
        String errorMessage = "Not Found";

        when(userService.getById(id)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void getById_WhenFound_ThenSuccess() throws Exception {
        User user = createTestUser();

        when(userService.getById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.startWeight").value(user.getStartWeight()))
                .andExpect(jsonPath("$.targetWeight").value(user.getTargetWeight()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updateUserStatus_WhenNotFound_Then404() throws Exception {
        long id = 1;
        DeletedState deletedState = DeletedState.NOT_DELETED;
        String errorMessage = "Not Found";

        when(userService.updateStatus(id, deletedState)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/users/status/" + id)
                        .param("newState", deletedState.name()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void updateUserStatus_WhenValidValue_ThenSuccess() throws Exception {
        User user = createTestUser();
        DeletedState deletedState = DeletedState.NOT_DELETED;

        when(userService.updateStatus(user.getId(), deletedState)).thenReturn(user);

        mockMvc.perform(put("/users/status/" + user.getId())
                        .param("newState", deletedState.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.deletedState").value(deletedState.toString()));
    }

    @Test
    void updateUserView_WhenNotFound_Then404() throws Exception {
        User user = createTestUser();
        UpdateUserView updateUserView = new UpdateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );
        String errorMessage = "Not Found";

        when(userService.update(user.getId(), updateUserView)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/users/update/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserView)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void updateUserView_WhenValidValues_ThenSuccess() throws Exception {
        User user = createTestUser();
        UpdateUserView updateUserView = new UpdateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );

        when(userService.update(user.getId(), updateUserView)).thenReturn(user);

        mockMvc.perform(put("/users/update/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserView)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.startWeight").value(user.getStartWeight()))
                .andExpect(jsonPath("$.targetWeight").value(user.getTargetWeight()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updateUserView_WhenInvalidValues_ThenBadRequest() throws Exception {
        User user = createTestUser();
        String badJson = """
                {
                    "name": 13,
                    "startWeight": 25,
                    "targetWeight": 250,
                    "email": "test"
                }
                """;

        mockMvc.perform(put("/users/update/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("startWeight")))
                .andExpect(jsonPath("$.message", containsString("targetWeight")))
                .andExpect(jsonPath("$.message", containsString("email")));
    }
}