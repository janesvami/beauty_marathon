package ani.beautymarathon.view.measurement;

import ani.beautymarathon.entity.ClosedState;

import java.time.LocalDate;
import java.util.List;

public record CascadeGetWkMeasurementView(
        Long id,
        LocalDate measurementDate,
        ClosedState closedState,
        List<CascadeGetUserMeasurementView> userMeasurements
) {}