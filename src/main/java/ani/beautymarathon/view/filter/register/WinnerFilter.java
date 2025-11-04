package ani.beautymarathon.view.filter.register;

public record WinnerFilter(
        WinnerMonthFilter month,
        WinnerUserFilter user
) {}