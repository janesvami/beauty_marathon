package ani.beautymarathon.controller;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.service.UserService;
import ani.beautymarathon.view.CreateUserView;
import ani.beautymarathon.view.GetUserView;
import ani.beautymarathon.view.UpdateUserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @Operation(summary = "Creating a new marathon racer",
            description = "Creating a new user and saving it in the DB with returning it to the client",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The user was created successfully"),
            })
    public GetUserView createUser(@Valid @RequestBody CreateUserView newUserView) {
        final User newUser = new User();
        newUser.setName(newUserView.name());
        newUser.setStartWeight(newUserView.startWeight());
        newUser.setTargetWeight(newUserView.targetWeight());
        newUser.setDeletedState(DeletedState.NOT_DELETED);
        newUser.setCreationDate(LocalDate.now());

        final User savedUser = userService.save(newUser);
        return constructUserView(savedUser);
    }

    @GetMapping("/all")
    public List<GetUserView> findAll() {
        final List<User> users = userService.findAll();
        return users.stream()
                .map(this::constructUserView)
                .toList();
    }

    @GetMapping("/{id}")
    public GetUserView getById(@PathVariable long id) {
        final User user = userService.getById(id);
        return constructUserView(user);
    }

    @PutMapping("/status/{id}")
    public GetUserView updateUserStatus(@PathVariable Long id, @RequestParam("newState") DeletedState newState) {
        final User updatedUser = userService.updateStatus(id, newState);
        return constructUserView(updatedUser);
    }

    @PutMapping("/update/{id}")
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