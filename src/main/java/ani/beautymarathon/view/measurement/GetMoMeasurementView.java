package ani.beautymarathon.view.measurement;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.NotNull;

public record GetMoMeasurementView(
        @NotNull
        Long id,
        @NotNull
        ClosedState closedState,
        @NotNull
        Integer year,
        @NotNull
        Integer monthNumber
) {}