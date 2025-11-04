package ani.beautymarathon.view.filter.register;

public record UserMeasurementFilter(
        RegisterUserFilter user,
        RegisterWeekFilter week
) {}