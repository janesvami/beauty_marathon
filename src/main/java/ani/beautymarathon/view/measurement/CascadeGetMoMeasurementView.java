package ani.beautymarathon.view.measurement;

import ani.beautymarathon.entity.ClosedState;

import java.util.List;

public record CascadeGetMoMeasurementView(
        Long id,
        ClosedState closedState,
        Integer year,
        Integer monthNumber,
        List<CascadeGetWkMeasurementView> wkMeasurements
) {}