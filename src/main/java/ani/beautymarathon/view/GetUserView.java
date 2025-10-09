package ani.beautymarathon.view;

import ani.beautymarathon.entity.DeletedState;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetUserView(
        @NotNull
        Long id,

        @NotNull
        String name,

        @NotNull
        BigDecimal startWeight,

        @NotNull
        BigDecimal targetWeight,

        @NotNull
        LocalDate creationDate,

        @NotNull
        DeletedState deletedState
) {}