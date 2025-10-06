package ani.beautymarathon.controller;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.UserMeasurement;
import ani.beautymarathon.entity.WkMeasurement;
import ani.beautymarathon.service.MeasurementService;
import ani.beautymarathon.view.CascadeGetUserView;
import ani.beautymarathon.view.GetUserView;
import ani.beautymarathon.view.measurement.CascadeGetMoMeasurementView;
import ani.beautymarathon.view.measurement.CascadeGetUserMeasurementView;
import ani.beautymarathon.view.measurement.CascadeGetWkMeasurementView;
import ani.beautymarathon.view.measurement.CreateUserMeasurementView;
import ani.beautymarathon.view.measurement.CreateWkMeasurementView;
import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.measurement.GetUserMeasurementView;
import ani.beautymarathon.view.measurement.GetWkMeasurementView;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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

    @GetMapping("/mo/all")
    public Page<CascadeGetMoMeasurementView> getCascadeOfAllMeasurements(Pageable pageable) {
        return measurementService.getCascadeOfAllMeasurements(pageable)
                .map(this::constructCascadeMoMeasurementView);
    }

    @GetMapping("/user/all")
    public Page<GetUserMeasurementView> getAllMeasurements(Pageable pageable) {
        return measurementService.getAllUserMeasurements(pageable)
                .map(this::constructUserMeasurementView);
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
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getTotalPoint(),
                userMeasurement.getCommentary(),
                wkMeasurementView,
                userView
        );
    }

    private CascadeGetUserMeasurementView constructCascadeUserMeasurementView(UserMeasurement userMeasurement) {
        final User user = userMeasurement.getUser();
        final CascadeGetUserView userView = new CascadeGetUserView(
                user.getId(),
                user.getName(),
                user.getDeletedState()
        );

        return new CascadeGetUserMeasurementView(
                userMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getTotalPoint(),
                userView
        );
    }

    private CascadeGetWkMeasurementView constructCascadeWkMeasurementView(WkMeasurement wkMeasurement) {
        final List<CascadeGetUserMeasurementView> userMeasurementViews = wkMeasurement.getUserMeasurements()
                .stream()
                .filter(um -> um.getUser().getDeletedState() == DeletedState.NOT_DELETED)
                .map(this::constructCascadeUserMeasurementView)
                .toList();
        return new CascadeGetWkMeasurementView(
                wkMeasurement.getId(),
                wkMeasurement.getMeasurementDate(),
                wkMeasurement.getClosedState(),
                userMeasurementViews
        );
    }

    private CascadeGetMoMeasurementView constructCascadeMoMeasurementView(MoMeasurement moMeasurement) {
        final List<CascadeGetWkMeasurementView> userMeasurementViews = moMeasurement.getWkMeasurements()
                .stream()
                .map(this::constructCascadeWkMeasurementView)
                .toList();
        return new CascadeGetMoMeasurementView(
                moMeasurement.getId(),
                moMeasurement.getClosedState(),
                moMeasurement.getYear(),
                moMeasurement.getMonthNumber(),
                userMeasurementViews
        );
    }
}