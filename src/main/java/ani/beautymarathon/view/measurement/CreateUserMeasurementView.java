package ani.beautymarathon.view.measurement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateUserMeasurementView(

        @NotNull
        @JsonProperty("wkMeasurementId")
        Long wkMeasurementId,

        @NotNull
        @JsonProperty("userId")
        Long userId,

        @Min(30)
        @Max(200)
        @NotNull
        @JsonProperty("weight")
        BigDecimal weight,

        @Min(0)
        @Max(5)
        @NotNull
        @JsonProperty("weightPoint")
        Integer weightPoint,

        @Min(0)
        @Max(10)
        @JsonProperty("sleepPoint")
        Integer sleepPoint,

        @Min(0)
        @Max(10)
        @JsonProperty("waterPoint")
        Integer waterPoint,

        @Min(0)
        @Max(10)
        @JsonProperty("stepPoint")
        Integer stepPoint,

        @Min(0)
        @Max(10)
        @JsonProperty("diaryPoint")
        Integer diaryPoint,

        @Min(0)
        @Max(10)
        @JsonProperty("alcoholFreePoints")
        Integer alcoholFreePoints,

        @JsonProperty("commentary")
        String commentary
) {}