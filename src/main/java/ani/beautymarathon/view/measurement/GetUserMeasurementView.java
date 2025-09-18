package ani.beautymarathon.view.measurement;

import ani.beautymarathon.view.GetUserView;

import java.math.BigDecimal;

public record GetUserMeasurementView(
        Long id,
        BigDecimal weight,
        Integer weightPoint,
        Integer sleepPoint,
        Integer waterPoint,
        Integer stepPoint,
        Integer diaryPoint,
        Integer alcoholFreePoints,
        String commentary,
        GetWkMeasurementView wkMeasurement,
        GetUserView user
) {}
