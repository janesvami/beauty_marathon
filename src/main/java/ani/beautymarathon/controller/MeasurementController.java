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
import ani.beautymarathon.view.measurement.filter.register.MoMeasurementFilter;
import ani.beautymarathon.view.measurement.filter.register.UserMeasurementFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(
        name = "Measurement controllers",
        description = "Controllers for all operations with measurements"
)
@RequestMapping("/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }


    @PostMapping("/wk/create")
    @Operation(summary = "Create a week measurement",
            description = """
                    This operation creates a new week_measurement for the corresponding date.
                    If the month_measurement for that date doesn't exist, it creates one as well.""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Week measurement is created",
                            content = @Content(schema = @Schema(implementation = GetWkMeasurementView.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public ResponseEntity<GetWkMeasurementView> createWkMeasurement(
            @Valid @RequestBody CreateWkMeasurementView newWkMeasurementView
    ) {
        final WkMeasurement newWkMeasurement = new WkMeasurement();

        newWkMeasurement.setMeasurementDate(newWkMeasurementView.measurementDate());
        newWkMeasurement.setCommentary(newWkMeasurementView.commentary());

        final WkMeasurement createdWkMeasurement = measurementService.createWkMeasurement(newWkMeasurement);
        final var wkMeasurementView = constructWeekMeasurementView(createdWkMeasurement);
        return ResponseEntity.status(201).body(wkMeasurementView);
    }

    @PostMapping("/user/create")
    @Operation(summary = "Create a user measurement",
            description = """
                    This operation creates a new user measurement for the corresponding date.""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User measurement is created",
                            content = @Content(schema = @Schema(implementation = GetUserMeasurementView.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "404", description = "User or WkMeasurement is not found",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })

    public ResponseEntity<GetUserMeasurementView> createUserMeasurement
            (@Valid @RequestBody CreateUserMeasurementView newUserMeasurementView) {

        final UserMeasurement createdUserMeasurement = measurementService.createUserMeasurement(newUserMeasurementView);
        final var userMeasurementView = constructUserMeasurementView(createdUserMeasurement);
        return ResponseEntity.status(201).body(userMeasurementView);
    }

    @PostMapping("/mo/all")
    @Operation(summary = "Get all months, weeks, and measurements in a cascade",
            description = """
                    This operation returns all measurements in a cascading structure with pagination and filtering.
                    The top-level element in the cascade is the month.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All months with all measurements are received",
                            content = @Content(schema = @Schema(implementation = CascadeGetMoMeasurementView.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public Page<CascadeGetMoMeasurementView> getCascadeOfAllMeasurements(
            @Valid @RequestBody MoMeasurementFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return measurementService.getCascadeOfAllMeasurements(filter, pageable)
                .map(this::constructCascadeMoMeasurementView);
    }

    @PostMapping("/user/all")
    @Operation(summary = "Get all measurements",
            description = """
                    This operation returns all measurements with pagination and filtering.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All measurements are received",
                            content = @Content(schema = @Schema(implementation = GetUserMeasurementView.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public Page<GetUserMeasurementView> getAllMeasurements(
            @Valid @RequestBody UserMeasurementFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return measurementService.getAllUserMeasurements(filter, pageable)
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