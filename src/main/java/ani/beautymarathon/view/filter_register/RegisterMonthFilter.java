package ani.beautymarathon.view.filter_register;

import ani.beautymarathon.entity.ClosedState;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RegisterMonthFilter(
        Integer year,

        @Max(12)
        @Min(1)
        Integer monthNumber,

        ClosedState closedState
) {}