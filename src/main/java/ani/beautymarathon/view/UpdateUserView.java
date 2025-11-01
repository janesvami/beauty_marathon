package ani.beautymarathon.view;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateUserView(

        @NotBlank
        @NotNull
        String name,

        @Min(30)
        @Max(200)
        @NotNull
        BigDecimal startWeight,

        @Min(30)
        @Max(200)
        @NotNull
        BigDecimal targetWeight,

        @NotNull
        String email
){}

