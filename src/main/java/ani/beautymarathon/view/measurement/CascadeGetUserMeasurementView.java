package ani.beautymarathon.view.measurement;

import ani.beautymarathon.view.user.CascadeGetUserView;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CascadeGetUserMeasurementView(
        @NotNull
        Long id,
        @NotNull
        BigDecimal weight,
        @NotNull
        Integer totalPoints,
        @NotNull
        CascadeGetUserView userView
) {}