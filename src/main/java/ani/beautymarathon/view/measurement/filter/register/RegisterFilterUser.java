package ani.beautymarathon.view.measurement.filter.register;

import ani.beautymarathon.entity.DeletedState;

public record RegisterFilterUser(
        String name,
        DeletedState deletedState
) {}