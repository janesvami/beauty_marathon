package ani.beautymarathon.view.measurement;
import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.NotNull;

public record UpdateWkMeasurementView(
        @NotNull
        ClosedState closedState,

        String commentary
) {}