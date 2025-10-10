package ani.beautymarathon.controller;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.service.UserService;
import ani.beautymarathon.view.CreateUserView;
import ani.beautymarathon.view.GetUserView;
import ani.beautymarathon.view.UpdateUserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(
        name = "User controllers",
        description = "Controllers for all operations with users"
)
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new marathon racer",
            description = """
                    This operation creates a new user and saves it in the database.""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "The user is created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<GetUserView> createUser(@Valid @RequestBody CreateUserView newUserView) {
        final User newUser = new User();
        newUser.setName(newUserView.name());
        newUser.setStartWeight(newUserView.startWeight());
        newUser.setTargetWeight(newUserView.targetWeight());
        newUser.setDeletedState(DeletedState.NOT_DELETED);
        newUser.setCreationDate(LocalDate.now());

        final User savedUser = userService.save(newUser);
        final var userView = constructUserView(savedUser);
        return ResponseEntity.status(201).body(userView);
    }

    @GetMapping("/all")
    @Operation(summary = "Get profiles of all users",
            description = """
                    This operation returns a list of profiles of all registered users.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profiles are received"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public List<GetUserView> findAll() {
        final List<User> users = userService.findAll();
        return users.stream()
                .map(this::constructUserView)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID",
            description = """
                    This operation returns the user profile for the given ID.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile is received"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "404", description = "User is not found",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public GetUserView getById(@PathVariable long id) {
        final User user = userService.getById(id);
        return constructUserView(user);
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "Update user status by ID",
            description = """
                     This operation updates the user status for the given ID to either "DELETED" or "NOT_DELETED".
                    The status "DELETED" means that the user is not currently participating in the project, but can be restored.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User status is updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public GetUserView updateUserStatus(@PathVariable Long id, @RequestParam("newState") DeletedState newState) {
        final User updatedUser = userService.updateStatus(id, newState);
        return constructUserView(updatedUser);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update user profile",
            description = """
                    This operation updates user profile data, excluding status changes.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile is updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public GetUserView updateUserView(@PathVariable Long id, @RequestBody UpdateUserView userView) {
        final User updatedUser = userService.update(id, userView);
        return constructUserView(updatedUser);
    }

    private GetUserView constructUserView(User user) {
        return new GetUserView(
                user.getId(),
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getCreationDate(),
                user.getDeletedState()
        );
    }
}