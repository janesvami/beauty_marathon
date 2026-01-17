package ani.beautymarathon.service;

import ani.beautymarathon.entity.ClosedState;
import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.UserMeasurement;
import ani.beautymarathon.entity.WkMeasurement;
import ani.beautymarathon.exception.MoClosedException;
import ani.beautymarathon.exception.UserDeletedException;
import ani.beautymarathon.exception.WkMeasurementClosedException;
import ani.beautymarathon.repository.MoMeasurementRepository;
import ani.beautymarathon.repository.UserMeasurementRepository;
import ani.beautymarathon.repository.UserRepository;
import ani.beautymarathon.repository.WinnerRepository;
import ani.beautymarathon.repository.WkMeasurementRepository;
import ani.beautymarathon.view.filter.register.MoMeasurementFilter;
import ani.beautymarathon.view.filter.register.RegisterUserFilter;
import ani.beautymarathon.view.filter.register.RegisterWeekFilter;
import ani.beautymarathon.view.filter.register.UserMeasurementFilter;
import ani.beautymarathon.view.measurement.CreateUserMeasurementView;
import ani.beautymarathon.view.measurement.UpdateUserMeasurementView;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ani.beautymarathon.MeasurementsTestHelper.createTestMoMeasurement;
import static ani.beautymarathon.MeasurementsTestHelper.createTestUserMeasurement;
import static ani.beautymarathon.MeasurementsTestHelper.createTestWkMeasurement;
import static ani.beautymarathon.UserTestHelper.createTestUser;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeasurementServiceTest {
    private final MoMeasurementRepository moMeasurementRepository = mock();
    private final WkMeasurementRepository wkMeasurementRepository = mock();
    private final UserMeasurementRepository userMeasurementRepository = mock();
    private final WinnerRepository winnerRepository = mock();
    private final UserRepository userRepository = mock();
    private final UserService userService = new UserService(userRepository);
    private final WinnerService winnerService = new WinnerService(winnerRepository, userService);
    private final MeasurementService measurementService = new MeasurementService(
            moMeasurementRepository,
            wkMeasurementRepository,
            userMeasurementRepository,
            userRepository,
            winnerRepository,
            winnerService
    );

    @Test
    void createWkMeasurement_WhenWkMeasurementIsAlreadyExists_ThenThrowIllegalArgumentException() {
        WkMeasurement wkMeasurement = createTestWkMeasurement();

        when(wkMeasurementRepository.findByMeasurementDate(wkMeasurement.getMeasurementDate()))
                .thenThrow(new IllegalArgumentException(
                        "WkMeasurement with date " + wkMeasurement.getMeasurementDate() + " already exists"
                ));
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> measurementService.createWkMeasurement(wkMeasurement)
        );
        String expectedMessage = "WkMeasurement with date " + wkMeasurement.getMeasurementDate() + " already exists";

        assertThat(ex.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void createWkMeasurement_WhenMoMeasurementIsClosed_ThenThrowMoClosedException() {
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        wkMeasurement.setClosedState(ClosedState.CLOSED);
        Integer year = wkMeasurement.getMoMeasurement().getYear();
        Integer monthNumber = wkMeasurement.getMoMeasurement().getMonthNumber();

        when(wkMeasurementRepository.findByMeasurementDate(any())).thenReturn(Optional.empty());
        when(moMeasurementRepository.findByYearAndMonthNumber(year, monthNumber))
                .thenReturn(Optional.of(wkMeasurement.getMoMeasurement()));
        when(wkMeasurementRepository.save(wkMeasurement))
                .thenThrow(new MoClosedException("Found month " + monthNumber + "-" + year + " is closed"));
        MoClosedException ex = assertThrows(
                MoClosedException.class,
                () -> measurementService.createWkMeasurement(wkMeasurement)
        );
        String expectedMessage = "Found month " + monthNumber + "-" + year + " is closed";

        assertThat(ex.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void createWkMeasurement_WhenCreatingIsSuccessful_ThenReturnsCreatedWkMeasurement() {
        WkMeasurement wkMeasurement = createTestWkMeasurement();

        when(wkMeasurementRepository.save(any())).thenReturn(wkMeasurement);
        WkMeasurement result = measurementService.createWkMeasurement(wkMeasurement);

        assertEquals(wkMeasurement, result);
        assertEquals(wkMeasurement.getId(), result.getId());
        assertEquals(wkMeasurement.getMoMeasurement(), result.getMoMeasurement());
        assertEquals(wkMeasurement.getMeasurementDate(), result.getMeasurementDate());
        assertEquals(wkMeasurement.getClosedState(), result.getClosedState());
        assertEquals(wkMeasurement.getCommentary(), result.getCommentary());
    }

    @Test
    void createUserMeasurement_WhenUserIsNotFound_ThenThrowEntityNotFoundException() {
        User user = createTestUser();
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        UserMeasurement userMeasurement = createTestUserMeasurement();
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                user.getId(),
                wkMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.createUserMeasurement(createUserMeasurementView)
        );
        String expectedMessage = "User not found";

        assertEquals(expectedMessage, entityNotFoundException.getMessage());
    }

    @Test
    void createUserMeasurement_WhenWkMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        User user = createTestUser();
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        UserMeasurement userMeasurement = createTestUserMeasurement();
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                user.getId(),
                wkMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.createUserMeasurement(createUserMeasurementView)
        );
        String expectedMessage = "WkMeasurement not found";

        assertEquals(expectedMessage, entityNotFoundException.getMessage());
    }

    @Test
    void createUserMeasurement_WhenWkMeasurementIsClosed_ThenThrowWkMeasurementClosedException() {
        User user = createTestUser();
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        UserMeasurement userMeasurement = createTestUserMeasurement();
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                user.getId(),
                wkMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.of(wkMeasurement));
        when(userMeasurementRepository.save(any()))
                .thenThrow(new WkMeasurementClosedException("The week is closed"));
        WkMeasurementClosedException ex = assertThrows(
                WkMeasurementClosedException.class,
                () -> measurementService.createUserMeasurement(createUserMeasurementView)
        );
        String expectedMessage = "The week is closed";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void createUserMeasurement_WhenUserIsDeleted_ThenThrowUserDeletedException() {
        User user = createTestUser();
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        UserMeasurement userMeasurement = createTestUserMeasurement();
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                user.getId(),
                wkMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.of(wkMeasurement));
        when(userMeasurementRepository.save(any()))
                .thenThrow(new UserDeletedException("User is deleted"));
        UserDeletedException ex = assertThrows(
                UserDeletedException.class,
                () -> measurementService.createUserMeasurement(createUserMeasurementView)
        );
        String expectedMessage = "User is deleted";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void createUserMeasurement_WhenCreatingIsSuccessful_ThenReturnsCreatedUserMeasurement() {
        User user = createTestUser();
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        UserMeasurement userMeasurement = createTestUserMeasurement();
        CreateUserMeasurementView createUserMeasurementView = new CreateUserMeasurementView(
                user.getId(),
                wkMeasurement.getId(),
                userMeasurement.getWeight(),
                userMeasurement.getWeightPoint(),
                userMeasurement.getSleepPoint(),
                userMeasurement.getWaterPoint(),
                userMeasurement.getStepPoint(),
                userMeasurement.getDiaryPoint(),
                userMeasurement.getAlcoholFreePoint(),
                userMeasurement.getCommentary()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.of(wkMeasurement));
        when(userMeasurementRepository.save(any())).thenReturn(userMeasurement);
        UserMeasurement result = measurementService.createUserMeasurement(createUserMeasurementView);

        assertEquals(userMeasurement, result);
        assertEquals(userMeasurement.getId(), result.getId());
        assertEquals(userMeasurement.getWeight(), result.getWeight());
        assertEquals(userMeasurement.getWeightPoint(), result.getWeightPoint());
        assertEquals(userMeasurement.getSleepPoint(), result.getSleepPoint());
        assertEquals(userMeasurement.getWaterPoint(), result.getWaterPoint());
        assertEquals(userMeasurement.getStepPoint(), result.getStepPoint());
        assertEquals(userMeasurement.getDiaryPoint(), result.getDiaryPoint());
        assertEquals(userMeasurement.getAlcoholFreePoint(), result.getAlcoholFreePoint());
        assertEquals(userMeasurement.getCommentary(), result.getCommentary());
    }

    @Test
    void getAllUserMeasurements_WhenMeasurementsAreFound_ThenReturnsAllMeasurements() {
        UserMeasurement userMeasurement1 = createTestUserMeasurement();
        UserMeasurement userMeasurement2 = createTestUserMeasurement();
        UserMeasurement userMeasurement3 = createTestUserMeasurement();
        RegisterUserFilter registerUserFilter = new RegisterUserFilter("Fesha", DeletedState.NOT_DELETED);
        RegisterWeekFilter registerWeekFilter = new RegisterWeekFilter(
                2025,
                9,
                ClosedState.OPEN,
                ClosedState.OPEN
        );
        UserMeasurementFilter userMeasurementFilter = new UserMeasurementFilter(
                registerUserFilter,
                registerWeekFilter
        );
        Pageable pageable = PageRequest.of(0, 5);
        List<UserMeasurement> measurements = new ArrayList<>();
        measurements.add(userMeasurement1);
        measurements.add(userMeasurement2);
        measurements.add(userMeasurement3);
        Page<UserMeasurement> mockPage = new PageImpl<>(measurements, pageable, 25);

        when(userMeasurementRepository.findAll(any(), eq(pageable))).thenReturn(mockPage);
        Page<UserMeasurement> result = measurementService.getAllUserMeasurements(userMeasurementFilter, pageable);

        assertThat(result.getContent()).hasSize(3);
        AssertionsForClassTypes.assertThat(result.getNumber()).isEqualTo(0);
        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(25);
        AssertionsForClassTypes.assertThat(result.getTotalPages()).isEqualTo(5);
        AssertionsForClassTypes.assertThat(result.isFirst()).isTrue();
        AssertionsForClassTypes.assertThat(result.hasNext()).isTrue();
    }

    @Test
    void getCascadeOfAllMeasurements_WhenMeasurementsAreFound_ThenReturnsAllMeasurements() {
        MoMeasurement moMeasurement1 = createTestMoMeasurement();
        MoMeasurement moMeasurement2 = createTestMoMeasurement();
        MoMeasurement moMeasurement3 = createTestMoMeasurement();
        MoMeasurementFilter moMeasurementFilter = new MoMeasurementFilter(ClosedState.OPEN, 2025, 9);
        Pageable pageable = PageRequest.of(0, 5);
        List<MoMeasurement> measurements = new ArrayList<>();
        measurements.add(moMeasurement1);
        measurements.add(moMeasurement2);
        measurements.add(moMeasurement3);
        Page<MoMeasurement> mockPage = new PageImpl<>(measurements, pageable, 25);

        when(moMeasurementRepository.findAll(any(), eq(pageable))).thenReturn(mockPage);
        Page<MoMeasurement> result = measurementService.getCascadeOfAllMeasurements(moMeasurementFilter, pageable);

        assertThat(result.getContent()).hasSize(3);
        AssertionsForClassTypes.assertThat(result.getNumber()).isEqualTo(0);
        AssertionsForClassTypes.assertThat(result.getTotalElements()).isEqualTo(25);
        AssertionsForClassTypes.assertThat(result.getTotalPages()).isEqualTo(5);
        AssertionsForClassTypes.assertThat(result.isFirst()).isTrue();
        AssertionsForClassTypes.assertThat(result.hasNext()).isTrue();
    }

    @Test
    void getWkById_WhenWkMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;

        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.getWkById(id)
        );
        String expectedMessage = "WkMeasurement with id " + id + " not found";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void getWkById_WhenWkMeasurementIsFound_ThenReturnsWkMeasurement() {
        WkMeasurement wkMeasurement = createTestWkMeasurement();

        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.of(wkMeasurement));
        WkMeasurement result = measurementService.getWkById(wkMeasurement.getId());

        assertEquals(wkMeasurement, result);
        assertEquals(wkMeasurement.getId(), result.getId());
        assertEquals(wkMeasurement.getMoMeasurement(), result.getMoMeasurement());
        assertEquals(wkMeasurement.getMeasurementDate(), result.getMeasurementDate());
        assertEquals(wkMeasurement.getClosedState(), result.getClosedState());
        assertEquals(wkMeasurement.getCommentary(), result.getCommentary());
    }

    @Test
    void updateWkStatus_WhenWkMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        WkMeasurement wkMeasurement = createTestWkMeasurement();

        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.updateWkStatus(wkMeasurement.getId(), wkMeasurement.getClosedState())
        );
        String expectedMessage = "WkMeasurement with id " + wkMeasurement.getId() + " not found";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void updateWkStatus_WhenUpdatingIsSuccessful_ThenReturnsUpdatedWkMeasurement() {
        WkMeasurement wkMeasurement = createTestWkMeasurement();

        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.of(wkMeasurement));
        when(wkMeasurementRepository.save(any())).thenReturn(wkMeasurement);
        WkMeasurement result = measurementService.updateWkStatus(wkMeasurement.getId(), wkMeasurement.getClosedState());

        assertEquals(wkMeasurement, result);
    }

    @Test
    void getMoById_WhenMoMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;

        when(moMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.getMoById(id)
        );
        String expectedMessage = "MoMeasurement with id " + id + " not found";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void getMoById_WhenMoMeasurementIsFound_ThenReturnsMoMeasurement() {
        MoMeasurement moMeasurement = createTestMoMeasurement();

        when(moMeasurementRepository.findById(any())).thenReturn(Optional.of(moMeasurement));
        MoMeasurement result = measurementService.getMoById(moMeasurement.getId());

        assertEquals(moMeasurement, result);
        assertEquals(moMeasurement.getId(), result.getId());
        assertEquals(moMeasurement.getMonthNumber(), result.getMonthNumber());
        assertEquals(moMeasurement.getYear(), result.getYear());
        assertEquals(moMeasurement.getClosedState(), result.getClosedState());
        assertEquals(moMeasurement.getMoDate(), result.getMoDate());
        assertEquals(moMeasurement.getWkMeasurements(), result.getWkMeasurements());
    }

    @Test
    void updateMoStatus__WhenMoMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        MoMeasurement moMeasurement = createTestMoMeasurement();

        when(moMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.updateMoStatus(moMeasurement.getId(), moMeasurement.getClosedState())
        );
        String expectedMessage = "MoMeasurement with id " + moMeasurement.getId() + " not found";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void updateMoStatus__WhenUpdatingIsSuccessful_ThenReturnsUpdatedWkMeasurement() {
        MoMeasurement moMeasurement = createTestMoMeasurement();

        when(moMeasurementRepository.findById(any())).thenReturn(Optional.of(moMeasurement));
        when(moMeasurementRepository.save(any())).thenReturn(moMeasurement);
        MoMeasurement result = measurementService.updateMoStatus(moMeasurement.getId(), moMeasurement.getClosedState());

        assertEquals(moMeasurement, result);
    }

    @Test
    void getMeasurementById_WhenMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;

        when(userMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.getMeasurementById(id)
        );
        String expectedMessage = "User measurement with id " + id + " not found";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void getMeasurementById_WhenMeasurementIsFound_ThenReturnsMeasurement() {
        UserMeasurement userMeasurement = createTestUserMeasurement();

        when(userMeasurementRepository.findById(any())).thenReturn(Optional.of(userMeasurement));
        UserMeasurement result = measurementService.getMeasurementById(userMeasurement.getId());

        assertEquals(userMeasurement, result);
        assertEquals(userMeasurement.getId(), result.getId());
        assertEquals(userMeasurement.getWkMeasurement(), result.getWkMeasurement());
        assertEquals(userMeasurement.getUser(), result.getUser());
        assertEquals(userMeasurement.getWaterPoint(), result.getWaterPoint());
        assertEquals(userMeasurement.getStepPoint(), result.getStepPoint());
        assertEquals(userMeasurement.getSleepPoint(), result.getSleepPoint());
        assertEquals(userMeasurement.getDiaryPoint(), result.getDiaryPoint());
        assertEquals(userMeasurement.getAlcoholFreePoint(), result.getAlcoholFreePoint());
        assertEquals(userMeasurement.getWeightPoint(), result.getWeightPoint());
        assertEquals(userMeasurement.getWeight(), result.getWeight());
        assertEquals(userMeasurement.getCommentary(), result.getCommentary());
    }

    @Test
    void updateMeasurement_WhenWkMeasurementIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;
        BigDecimal weight = new BigDecimal(55);
        UpdateUserMeasurementView userMeasurementView = new UpdateUserMeasurementView(
                weight,
                5,
                5,
                5,
                5,
                5,
                5,
                "test"
        );

        when(wkMeasurementRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> measurementService.updateMeasurement(id, userMeasurementView)
        );
        String expectedMessage = "User measurement with id " + id + " not found";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void updateMeasurement_WhenUpdatingIsSuccessful_ThenReturnsUpdatedWkMeasurement() {
        UserMeasurement userMeasurement = createTestUserMeasurement();
        BigDecimal weight = new BigDecimal(55);
        UpdateUserMeasurementView userMeasurementView = new UpdateUserMeasurementView(
                weight,
                5,
                5,
                5,
                5,
                5,
                5,
                "test"
        );

        when(userMeasurementRepository.findById(any())).thenReturn(Optional.of(userMeasurement));
        when(userMeasurementRepository.save(any())).thenReturn(userMeasurement);
        UserMeasurement result = measurementService.updateMeasurement(userMeasurement.getId(), userMeasurementView);

        assertEquals(userMeasurement, result);
        assertEquals(userMeasurement.getId(), result.getId());
        assertEquals(userMeasurement.getWkMeasurement(), result.getWkMeasurement());
        assertEquals(userMeasurement.getUser(), result.getUser());
        assertEquals(userMeasurement.getWaterPoint(), result.getWaterPoint());
        assertEquals(userMeasurement.getStepPoint(), result.getStepPoint());
        assertEquals(userMeasurement.getSleepPoint(), result.getSleepPoint());
        assertEquals(userMeasurement.getDiaryPoint(), result.getDiaryPoint());
        assertEquals(userMeasurement.getAlcoholFreePoint(), result.getAlcoholFreePoint());
        assertEquals(userMeasurement.getWeightPoint(), result.getWeightPoint());
        assertEquals(userMeasurement.getWeight(), result.getWeight());
        assertEquals(userMeasurement.getCommentary(), result.getCommentary());
    }
}