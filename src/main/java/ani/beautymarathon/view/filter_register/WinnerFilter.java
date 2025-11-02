package ani.beautymarathon.view.filter_register;

public record WinnerFilter(
        RegisterMonthFilter month,
        WinnerUserFilter user
) {}