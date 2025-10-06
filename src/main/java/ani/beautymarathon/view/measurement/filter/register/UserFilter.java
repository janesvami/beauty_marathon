package ani.beautymarathon.view.measurement.filter.register;

import ani.beautymarathon.entity.DeletedState;

public record UserFilter(
        String name,
        DeletedState state
) {}