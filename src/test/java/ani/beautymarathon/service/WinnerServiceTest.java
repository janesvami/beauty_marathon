package ani.beautymarathon.service;

import ani.beautymarathon.entity.ClosedState;
import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.repository.UserRepository;
import ani.beautymarathon.repository.WinnerRepository;
import ani.beautymarathon.view.filter.register.WinnerFilter;
import ani.beautymarathon.view.filter.register.WinnerMonthFilter;
import ani.beautymarathon.view.filter.register.WinnerUserFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ani.beautymarathon.MeasurementsTestHelper.createTestMoMeasurement;
import static ani.beautymarathon.MeasurementsTestHelper.createTestUser;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WinnerServiceTest {

    private final WinnerRepository winnerRepository = mock();
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);
    private final WinnerService winnerService = new WinnerService(winnerRepository, userService);

    @Test
    void getById_WhenWinnerIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;
        when(winnerRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> winnerService.getById(id)
        );
        String expectedMessage = "Winner with id " + id + " not found";
        assertEquals(expectedMessage, entityNotFoundException.getMessage());
    }

    @Test
    void getById_WhenWinnerIsFound_ThenReturnsWinner() {
        long id = 1;
        LocalDate today = LocalDate.now();
        MoMeasurement testMoMeasurement = createTestMoMeasurement();
        User testUser = createTestUser();
        Winner winner = new Winner();
        winner.setId(id);
        winner.setMoMeasurement(testMoMeasurement);
        winner.setUser(testUser);
        winner.setAveragePoint(45.2);
        winner.setCreationDate(today);

        when(winnerRepository.findById(any())).thenReturn(Optional.of(winner));
        Winner result = winnerService.getById(id);

        Winner expectedWinner = new Winner();
        expectedWinner.setId(id);
        expectedWinner.setMoMeasurement(testMoMeasurement);
        expectedWinner.setUser(testUser);
        expectedWinner.setAveragePoint(45.2);
        expectedWinner.setCreationDate(today);

        assertEquals(expectedWinner, result);
        assertEquals(id, result.getId());
        assertEquals(testMoMeasurement, result.getMoMeasurement());
        assertEquals(testUser, result.getUser());
        assertEquals(45.2, result.getAveragePoint());
        assertEquals(today, result.getCreationDate());
    }

    @Test
    void getAllWinners_WhenWinnersAreFound_ThenReturnsAllWinners() {
        long id = 1;
        LocalDate today = LocalDate.now();
        MoMeasurement testMoMeasurement = createTestMoMeasurement();
        User testUser = createTestUser();
        Winner winnerFirst = new Winner();
        winnerFirst.setId(id);
        winnerFirst.setMoMeasurement(testMoMeasurement);
        winnerFirst.setUser(testUser);
        winnerFirst.setAveragePoint(45.2);
        winnerFirst.setCreationDate(today);

        long id2 = 1;
        LocalDate today2 = LocalDate.now();
        MoMeasurement testMoMeasurement2 = createTestMoMeasurement();
        User testUser2 = createTestUser();
        Winner winnerSecond = new Winner();
        winnerSecond.setId(id2);
        winnerSecond.setMoMeasurement(testMoMeasurement2);
        winnerSecond.setUser(testUser2);
        winnerSecond.setAveragePoint(45.2);
        winnerSecond.setCreationDate(today2);

        WinnerMonthFilter winnerMonthFilter = new WinnerMonthFilter(2025, 9, ClosedState.CLOSED);
        WinnerUserFilter winnerUserFilter = new WinnerUserFilter("Fesha", DeletedState.NOT_DELETED);
        WinnerFilter winnerFilter = new WinnerFilter(winnerMonthFilter, winnerUserFilter);
        Pageable pageable = PageRequest.of(0, 5);

        List<Winner> expectedWinners = Arrays.asList(winnerFirst, winnerSecond);
        Page<Winner> mockPage = new PageImpl<>(expectedWinners, pageable, 25);

        when(winnerRepository.findAll(any(), eq(pageable)))
                .thenReturn(mockPage);

        Page<Winner> result = winnerService.getAllWinners(winnerFilter, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(25);
        assertThat(result.getTotalPages()).isEqualTo(5);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }
}