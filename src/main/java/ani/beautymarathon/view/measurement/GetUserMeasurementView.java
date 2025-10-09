package ani.beautymarathon.view.measurement;

import ani.beautymarathon.view.GetUserView;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GetUserMeasurementView(
        @NotNull
        Long id,

        @NotNull
        BigDecimal weight,

        @NotNull
        Integer weightPoint,

        @NotNull
        Integer sleepPoint,

        @NotNull
        Integer waterPoint,

        @NotNull
        Integer stepPoint,

        @NotNull
        Integer diaryPoint,

        @NotNull
        Integer alcoholFreePoints,

        @NotNull
        Integer totalPoints,

        String commentary,

        @NotNull
        GetWkMeasurementView wkMeasurement,

        @NotNull
        GetUserView user
) {}
