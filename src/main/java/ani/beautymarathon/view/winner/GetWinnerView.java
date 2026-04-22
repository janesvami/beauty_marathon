package ani.beautymarathon.view.winner;

import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.user.GetUserView;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GetWinnerView(
        @NotNull
        Long id,

        @NotNull
        GetMoMeasurementView moMeasurement,

        @NotNull
        GetUserView user,

        @NotNull
        Double averagePoint,

        @NotNull
        LocalDate creationDate
) {}