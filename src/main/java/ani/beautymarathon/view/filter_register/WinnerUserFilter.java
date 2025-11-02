package ani.beautymarathon.view.filter_register;

import ani.beautymarathon.entity.DeletedState;

public record WinnerUserFilter(
        String name,
        DeletedState deletedState
) {}