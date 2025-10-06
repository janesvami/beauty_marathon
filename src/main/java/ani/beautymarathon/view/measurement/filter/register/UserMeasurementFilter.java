package ani.beautymarathon.view.measurement.filter.register;

public record UserMeasurementFilter(
        UserFilter userFilter,
        WeekFilter weekFilter
) {}