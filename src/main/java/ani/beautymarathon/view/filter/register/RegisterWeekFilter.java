package ani.beautymarathon.view.filter.register;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RegisterWeekFilter(
        Integer year,

        @Max(12)
        @Min(1)
        Integer month,
        ClosedState wkClosedState,
        ClosedState moClosedState
) {}