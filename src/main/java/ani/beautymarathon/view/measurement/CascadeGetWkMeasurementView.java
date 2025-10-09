package ani.beautymarathon.view.measurement;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CascadeGetWkMeasurementView(
        @NotNull
        Long id,
        @NotNull
        LocalDate measurementDate,
        @NotNull
        ClosedState closedState,
        @NotNull
        List<CascadeGetUserMeasurementView> userMeasurements
) {}