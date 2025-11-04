package ani.beautymarathon.view.filter.register;

import ani.beautymarathon.entity.DeletedState;

public record RegisterUserFilter(
        String name,
        DeletedState deletedState
) {}