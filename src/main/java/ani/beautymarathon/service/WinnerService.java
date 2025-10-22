package ani.beautymarathon.service;

import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.repository.UserRepository;
import ani.beautymarathon.repository.WinnerRepository;
import ani.beautymarathon.view.UserMaxAverageView;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WinnerService {
    private final WinnerRepository winnerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public WinnerService(WinnerRepository winnerRepository, UserRepository userRepository, UserService userService) {
        this.winnerRepository = winnerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void createWinnersFromViews(List<UserMaxAverageView> userMaxAverageViews, MoMeasurement moMeasurement) {
        List<Winner> winners = new ArrayList<>();

        for (UserMaxAverageView userMax : userMaxAverageViews) {
            Winner winner = new Winner();
            winner.setAveragePoint(userMax.getMaxAverageTotal());
            winner.setUser(userService.getById(userMax.getUserId()));
            winner.setMoMeasurement(moMeasurement);

            winners.add(winner);
        }

        winnerRepository.saveAll(winners);
    }

}