package ani.beautymarathon.view.measurement.filter.register;

import ani.beautymarathon.entity.ClosedState;

public record MoMeasurementFilter(
        ClosedState closedState,
        Integer year,
        Integer monthNumber
) {}