package ani.beautymarathon.view.measurement.filter.register;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RegisterFilterWeek(
        Integer year,

        @Max(12)
        @Min(1)
        Integer month,
        ClosedState wkClosedState,
        ClosedState moClosedState
) {}