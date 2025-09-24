package ani.beautymarathon.view;

import ani.beautymarathon.entity.DeletedState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusView(
        @NotBlank
        @NotNull
        DeletedState deletedState
) {}