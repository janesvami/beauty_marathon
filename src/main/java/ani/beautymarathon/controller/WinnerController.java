package ani.beautymarathon.controller;

import ani.beautymarathon.service.WinnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}