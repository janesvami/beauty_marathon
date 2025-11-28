package ani.beautymarathon.controller;

import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.service.WinnerService;
import ani.beautymarathon.view.filter.register.WinnerFilter;
import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.user.GetUserView;
import ani.beautymarathon.view.winner.GetWinnerView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Winner controllers",
        description = "Controllers for all operations with winners"
)
@RequestMapping("/winners")
public class WinnerController {

    private final WinnerService winnerService;

    public WinnerController(WinnerService winnerService) {
        this.winnerService = winnerService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get winner profile by ID",
            description = """
                    This operation returns the winner profile for the given ID.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Winner profile is received"),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "404", description = "Winner is not found",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public GetWinnerView getById(@PathVariable long id) {
        final Winner winner = winnerService.getById(id);
        return constructWinnerView(winner);
    }

    @PostMapping("/all")
    @Operation(summary = "Get all winners",
            description = """
                    This operation returns all winners with pagination and filtering.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All winners are received",
                            content = @Content(schema = @Schema(implementation = GetWinnerView.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema()))
            })
    public Page<GetWinnerView> getAllWinners(
            @Valid @RequestBody WinnerFilter filter,
            @ParameterObject Pageable pageable
    ) {
        return winnerService.getAllWinners(filter, pageable)
                .map(this::constructWinnerView);
    }

    private GetWinnerView constructWinnerView(Winner winner) {
        final MoMeasurement moMeasurement = winner.getMoMeasurement();
        final GetMoMeasurementView moMeasurementView = new GetMoMeasurementView(
                moMeasurement.getId(),
                moMeasurement.getClosedState(),
                moMeasurement.getYear(),
                moMeasurement.getMonthNumber()
        );
        final User user = winner.getUser();
        final GetUserView userView = new GetUserView(
                user.getId(),
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getCreationDate(),
                user.getDeletedState(),
                user.getEmail()
        );

        return new GetWinnerView(
                winner.getId(),
                moMeasurementView,
                userView,
                winner.getAveragePoint(),
                winner.getCreationDate()
        );
    }
}