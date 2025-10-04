package ani.beautymarathon.view;

import ani.beautymarathon.entity.DeletedState;

public record CascadeGetUserView(
        Long id,
        String name,
        DeletedState deletedState
) {}