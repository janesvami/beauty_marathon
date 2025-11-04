package ani.beautymarathon.view.filter.register;

import ani.beautymarathon.entity.DeletedState;

public record WinnerUserFilter(
        String name,
        DeletedState deletedState
) {}