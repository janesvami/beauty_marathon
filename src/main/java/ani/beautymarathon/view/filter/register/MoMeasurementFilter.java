package ani.beautymarathon.view.filter.register;

import ani.beautymarathon.entity.ClosedState;

public record MoMeasurementFilter(
        ClosedState closedState,
        Integer year,
        Integer monthNumber
) {}