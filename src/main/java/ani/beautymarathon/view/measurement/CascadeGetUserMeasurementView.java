package ani.beautymarathon.view.measurement;

import ani.beautymarathon.view.CascadeGetUserView;

import java.math.BigDecimal;

public record CascadeGetUserMeasurementView(
        Long id,
        BigDecimal weight,
        Integer totalPoints,
        CascadeGetUserView userView
) {}