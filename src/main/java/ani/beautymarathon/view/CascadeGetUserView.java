package ani.beautymarathon.view;

import ani.beautymarathon.entity.DeletedState;
import jakarta.validation.constraints.NotNull;

public record CascadeGetUserView(
        @NotNull
        Long id,

        @NotNull
        String name,

        @NotNull
        DeletedState deletedState
) {}