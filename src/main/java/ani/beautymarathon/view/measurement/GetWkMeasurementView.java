package ani.beautymarathon.view.measurement;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GetWkMeasurementView(
        @NotNull
        Long id,

        @NotNull
        LocalDate measurementDate,

        @NotNull
        ClosedState closedState,

        String commentary,

        @NotNull
        GetMoMeasurementView moMeasurement
) {}