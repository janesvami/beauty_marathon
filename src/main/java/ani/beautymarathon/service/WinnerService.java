package ani.beautymarathon.service;

import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.repository.WinnerRepository;
import ani.beautymarathon.view.UserMaxAverageView;
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
}