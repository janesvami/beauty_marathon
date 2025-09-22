package ani.beautymarathon.controller;

import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.UserMeasurement;
import ani.beautymarathon.entity.WkMeasurement;
import ani.beautymarathon.service.MeasurementService;
import ani.beautymarathon.view.GetUserView;
import ani.beautymarathon.view.measurement.CreateUserMeasurementView;
import ani.beautymarathon.view.measurement.CreateWkMeasurementView;
import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.measurement.GetUserMeasurementView;
import ani.beautymarathon.view.measurement.GetWkMeasurementView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping("/wk/create")
    public GetWkMeasurementView createWkMeasurement(@Valid @RequestBody CreateWkMeasurementView newWkMeasurementView) {
        final WkMeasurement newWkMeasurement = new WkMeasurement();

        newWkMeasurement.setMeasurementDate(newWkMeasurementView.measurementDate());
        newWkMeasurement.setCommentary(newWkMeasurementView.commentary());

        final WkMeasurement createdWkMeasurement = measurementService.createWkMeasurement(newWkMeasurement);
        return constructWeekMeasurementView(createdWkMeasurement);
    }

    @PostMapping("/user/create")
    public GetUserMeasurementView createUserMeasurement
            (@Valid @RequestBody CreateUserMeasurementView newUserMeasurementView) {

        final UserMeasurement createdUserMeasurement = measurementService.createUserMeasurement(newUserMeasurementView);
        return constructUserMeasurementView(createdUserMeasurement);
    }

    private GetWkMeasurementView constructWeekMeasurementView(WkMeasurement wkMeasurement) {
        final MoMeasurement moMeasurement = wkMeasurement.getMoMeasurement();
        final GetMoMeasurementView moMeasurementView = new GetMoMeasurementView(
                moMeasurement.getId(),
                moMeasurement.getClosedState(),
                moMeasurement.getYear(),
                moMeasurement.getMonthNumber()
        );
        return new GetWkMeasurementView(
                wkMeasurement.getId(),
                wkMeasurement.getMeasurementDate(),
                wkMeasurement.getClosedState(),
                wkMeasurement.getCommentary(),
                moMeasurementView
        );
    }

    private GetUserMeasurementView constructUserMeasurementView(UserMeasurement userMeasurement) {

        final WkMeasurement wkMeasurement = userMeasurement.getWkMeasurement();
        final MoMeasurement moMeasurement = wkMeasurement.getMoMeasurement();
        final GetMoMeasurementView moMeasurementView = new GetMoMeasurementView(
                moMeasurement.getId(),
                moMeasurement.getClosedState(),
                moMeasurement.getYear(),
                moMeasurement.getMonthNumber()
        );
        final GetWkMeasurementView wkMeasurementView = new GetWkMeasurementView(
                wkMeasurement.getId(),
                wkMeasurement.getMeasurementDate(),
                wkMeasurement.getClosedState(),
                wkMeasurement.getCommentary(),
                moMeasurementView
        );
        final User user = userMeasurement.getUser();
        final GetUserView userView = new GetUserView(
                user.getId(),
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getCreationDate(),
                user.getDeletedState()
        );
        return new GetUserMeasurementView(
                userMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoints(),
                userMeasurement.getCommentary(),
                wkMeasurementView,
                userView
        );
    }
}
