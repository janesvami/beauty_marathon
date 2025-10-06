package ani.beautymarathon.view.measurement.filter.register;

import ani.beautymarathon.entity.ClosedState;

public record WeekFilter(
        Integer year,
        Integer month,
        ClosedState wkClosedState,
        ClosedState moClosedState
) {}