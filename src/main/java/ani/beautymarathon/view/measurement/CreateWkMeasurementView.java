package ani.beautymarathon.view.measurement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateWkMeasurementView(
        @NotNull
        @JsonProperty("measurementDate")
        LocalDate measurementDate,

        @JsonProperty("commentary")
        String commentary
){}