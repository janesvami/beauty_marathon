package ani.beautymarathon.view.filter_register;

public record WinnerFilter(
        WinnerMonthFilter month,
        WinnerUserFilter user
) {}