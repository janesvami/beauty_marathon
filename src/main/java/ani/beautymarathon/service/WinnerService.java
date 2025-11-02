package ani.beautymarathon.service;

import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.repository.WinnerRepository;
import ani.beautymarathon.view.filter_register.WinnerFilter;
import ani.beautymarathon.view.user.UserMaxAverageView;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WinnerService {
    private final WinnerRepository winnerRepository;
    private final UserService userService;

    public WinnerService(WinnerRepository winnerRepository, UserService userService) {
        this.winnerRepository = winnerRepository;
        this.userService = userService;
    }

    public void createWinnersFromViews(List<UserMaxAverageView> userMaxAverageViews, MoMeasurement moMeasurement) {
        final List<Winner> winners = new ArrayList<>();

        for (UserMaxAverageView userMax : userMaxAverageViews) {
            final Winner winner = new Winner();
            winner.setAveragePoint(userMax.getMaxAverageTotal());
            final Long userMaxId = userMax.getUserId();
            final User winnerUserId = userService.getById(userMaxId);
            winner.setUser(winnerUserId);
            winner.setMoMeasurement(moMeasurement);

            winners.add(winner);
        }
        winnerRepository.saveAll(winners);
    }

    public Page<Winner> getAllWinners(WinnerFilter filter, Pageable pageable) {
        if (filter != null) {
            return searchWinnersByQbe(filter, pageable);
        } else {
            return winnerRepository.findAll(pageable);
        }
    }

    private Page<Winner> searchWinnersByQbe(WinnerFilter filter, Pageable pageable) {
        final var probe = new Winner();
        final var filterUser = filter.user();
        final var filterMonth = filter.month();

        if (filterUser != null) {
            final var user = new User();
            user.setName(filterUser.name());
            user.setDeletedState(filterUser.deletedState());
            probe.setUser(user);
        }
        if (filterMonth != null) {
            final var month = new MoMeasurement();
            month.setYear(filterMonth.year());
            month.setMonthNumber(filterMonth.monthNumber());
            month.setClosedState(filterMonth.closedState());
            probe.setMoMeasurement(month);
        }

        final ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher(
                        "user.name",
                        matcher -> matcher.ignoreCase().contains()
                );

        final Example<Winner> example = Example.of(probe, exampleMatcher);

        return winnerRepository.findAll(example, pageable);
    }
}