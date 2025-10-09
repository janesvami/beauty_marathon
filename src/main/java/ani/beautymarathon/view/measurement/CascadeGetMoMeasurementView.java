package ani.beautymarathon.view.measurement;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CascadeGetMoMeasurementView(
        @NotNull
        Long id,
        @NotNull
        ClosedState closedState,
        @NotNull
        Integer year,
        @NotNull
        Integer monthNumber,
        @NotNull
        List<CascadeGetWkMeasurementView> wkMeasurements
) {}