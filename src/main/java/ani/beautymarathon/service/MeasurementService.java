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
import ani.beautymarathon.view.filter_register.MoMeasurementFilter;
import ani.beautymarathon.view.filter_register.UserMeasurementFilter;
import ani.beautymarathon.view.measurement.CreateUserMeasurementView;
import ani.beautymarathon.view.measurement.UpdateUserMeasurementView;
import ani.beautymarathon.view.user.UserMaxAverageView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MeasurementService {
    private final MoMeasurementRepository moMeasurementRepository;
    private final WkMeasurementRepository wkMeasurementRepository;
    private final UserMeasurementRepository userMeasurementRepository;
    private final UserRepository userRepository;
    private final WinnerRepository winnerRepository;
    private final WinnerService winnerService;

    public MeasurementService(MoMeasurementRepository moMeasurementRepository,
                              WkMeasurementRepository wkMeasurementRepository,
                              UserMeasurementRepository userMeasurementRepository, UserRepository userRepository,
                              WinnerRepository winnerRepository, WinnerService winnerService) {
        this.moMeasurementRepository = moMeasurementRepository;
        this.wkMeasurementRepository = wkMeasurementRepository;
        this.userMeasurementRepository = userMeasurementRepository;
        this.userRepository = userRepository;
        this.winnerRepository = winnerRepository;
        this.winnerService = winnerService;
    }

    @Transactional
    public WkMeasurement createWkMeasurement(WkMeasurement wkMeasurement) {
        final LocalDate measurementDate = wkMeasurement.getMeasurementDate();
        final int year = measurementDate.getYear();
        final int month = measurementDate.getMonthValue();
        Optional<WkMeasurement> byMeasurementDate = wkMeasurementRepository.findByMeasurementDate(measurementDate);
        if (byMeasurementDate.isPresent()) {
            throw new IllegalArgumentException("WkMeasurement with date " + measurementDate + " already exists");
        }
        Optional<MoMeasurement> foundMonth = moMeasurementRepository.findByYearAndMonthNumber(year, month);
        foundMonth.ifPresentOrElse(
                (moMeasurement) -> {
                    ClosedState closedState = moMeasurement.getClosedState();
                    if (ClosedState.CLOSED == closedState) {
                        String errorText = "Found month " + month + "-" + year + " is closed";
                        throw new MoClosedException(errorText);
                    }
                    wkMeasurement.setMoMeasurement(moMeasurement);
                },
                () -> {
                    MoMeasurement moMeasurement = new MoMeasurement();
                    moMeasurement.setMoDate(measurementDate);
                    MoMeasurement savedMonth = moMeasurementRepository.save(moMeasurement);
                    log.info("Saved month: {}", savedMonth);
                    wkMeasurement.setMoMeasurement(savedMonth);
                }
        );
        WkMeasurement saved = wkMeasurementRepository.save(wkMeasurement);
        log.info("Saved week: {}", saved);
        return saved;
    }

    @Transactional
    public UserMeasurement createUserMeasurement
            (CreateUserMeasurementView newUserMeasurementView) {
        final UserMeasurement newUserMeasurement = new UserMeasurement();

        final User user = userRepository.findById(newUserMeasurementView.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        final WkMeasurement wkMeasurement = wkMeasurementRepository
                .findById(newUserMeasurementView.wkMeasurementId())
                .orElseThrow(() -> new EntityNotFoundException("WkMeasurement not found"));

        if (user.getDeletedState() == DeletedState.DELETED) {
            throw new UserDeletedException("User is deleted");
        }
        if (wkMeasurement.getClosedState() == ClosedState.CLOSED) {
            throw new WkMeasurementClosedException("The week is closed");
        }

        newUserMeasurement.setUser(user);
        newUserMeasurement.setWkMeasurement(wkMeasurement);
        newUserMeasurement.setWeight(newUserMeasurementView.weight());
        newUserMeasurement.setCommentary(newUserMeasurementView.commentary());
        newUserMeasurement.setDiaryPoint(newUserMeasurementView.diaryPoint());
        newUserMeasurement.setAlcoholFreePoint(newUserMeasurementView.alcoholFreePoints());
        newUserMeasurement.setSleepPoint(newUserMeasurementView.sleepPoint());
        newUserMeasurement.setStepPoint(newUserMeasurementView.stepPoint());
        newUserMeasurement.setWaterPoint(newUserMeasurementView.waterPoint());
        newUserMeasurement.setWeightPoint(newUserMeasurementView.weightPoint());

        return save(newUserMeasurement);
    }

    public Page<UserMeasurement> getAllUserMeasurements(UserMeasurementFilter filter, Pageable pageable) {
        if (filter != null) {
            return searchUserMeasurementsByQbe(filter, pageable);
        } else {
            return userMeasurementRepository.findAll(pageable);
        }
    }

    public Page<MoMeasurement> getCascadeOfAllMeasurements(MoMeasurementFilter filter, Pageable pageable) {
        if (filter != null) {
            return searchMoMeasurementsByQbe(filter, pageable);
        } else {
            return moMeasurementRepository.findAll(pageable);
        }
    }

    public WkMeasurement getWkById(long id) {
        return wkMeasurementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WkMeasurement with id " + id + " not found"));
    }

    public WkMeasurement updateWkStatus(long id, ClosedState closedState) {
        final WkMeasurement wkMeasurement = getWkById(id);
        wkMeasurement.setClosedState(closedState);

        final WkMeasurement updatedWkMeasurement = wkMeasurementRepository.save(wkMeasurement);
        log.info("Status of week with id {} has been updated to {}", id, updatedWkMeasurement.getClosedState());
        return updatedWkMeasurement;
    }

    public MoMeasurement getMoById(long id) {
        return moMeasurementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MoMeasurement with id " + id + " not found"));
    }

    @Transactional
    public MoMeasurement updateMoStatus(long moId, ClosedState newMoState) {
        final MoMeasurement moMeasurement = getMoById(moId);
        final ClosedState currentMoState = moMeasurement.getClosedState();

        if (currentMoState == newMoState) {
            return moMeasurement;
        } else {
            moMeasurement.setClosedState(newMoState);
            final MoMeasurement updatedMoMeasurement = moMeasurementRepository.save(moMeasurement);
            log.info("Status of month with id {} has been updated to {}", moId, updatedMoMeasurement.getClosedState());

            if (ClosedState.CLOSED == newMoState) {
                List<UserMaxAverageView> userMaxAverageViews = winnerRepository.findUsersWithMaxAverage(moId);
                winnerService.createWinnersFromViews(userMaxAverageViews, moMeasurement);
                log.info("The winner of the month has been determined!");
            }
            return updatedMoMeasurement;
        }
    }

    public UserMeasurement getMeasurementById(long id) {
        return userMeasurementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User measurement with id " + id + " not found"));
    }

    public UserMeasurement updateMeasurement(
            long id, UpdateUserMeasurementView userMeasurementView
    ) {

        final UserMeasurement userMeasurement = getMeasurementById(id);
        final WkMeasurement wkMeasurement = userMeasurement.getWkMeasurement();

        if(ClosedState.CLOSED.equals(wkMeasurement.getClosedState())) {
            throw new WkMeasurementClosedException(
                    "The week is closed.");
        }

        userMeasurement.setWeight(userMeasurementView.weight());
        userMeasurement.setWaterPoint(userMeasurementView.waterPoint());
        userMeasurement.setCommentary(userMeasurementView.commentary());
        userMeasurement.setDiaryPoint(userMeasurementView.diaryPoint());
        userMeasurement.setAlcoholFreePoint(userMeasurementView.alcoholFreePoints());
        userMeasurement.setSleepPoint(userMeasurementView.sleepPoint());
        userMeasurement.setStepPoint(userMeasurementView.stepPoint());
        userMeasurement.setWaterPoint(userMeasurementView.waterPoint());

        final UserMeasurement updatedUserMeasurement = userMeasurementRepository.save(userMeasurement);
        log.info("User with id {} has been updated {}", id, updatedUserMeasurement);
        return updatedUserMeasurement;
    }


    private Page<UserMeasurement> searchUserMeasurementsByQbe(UserMeasurementFilter filter, Pageable pageable) {
        final var probe = new UserMeasurement();
        final var filterUser = filter.user();
        final var filterWeek = filter.week();

        if (filterUser != null) {
            final var user = new User();
            user.setName(filterUser.name());
            user.setDeletedState(filterUser.deletedState());
            probe.setUser(user);
        }
        if (filterWeek != null) {
            final var mo = new MoMeasurement();
            mo.setYear(filterWeek.year());
            mo.setMonthNumber(filterWeek.month());
            mo.setClosedState(filterWeek.moClosedState());

            final var wk = new WkMeasurement();
            wk.setMoMeasurement(mo);
            wk.setClosedState(filterWeek.wkClosedState());
            probe.setWkMeasurement(wk);
        }

        final ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher(
                        "user.name",
                        matcher -> matcher.ignoreCase().contains()
                );

        final Example<UserMeasurement> example = Example.of(probe, exampleMatcher);

        return userMeasurementRepository.findAll(example, pageable);
    }

    private Page<MoMeasurement> searchMoMeasurementsByQbe(MoMeasurementFilter filter, Pageable pageable) {

        final var probe = new MoMeasurement();
        probe.setYear(filter.year());
        probe.setMonthNumber(filter.monthNumber());
        probe.setClosedState(filter.closedState());

        final ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues();
        final Example<MoMeasurement> example = Example.of(probe, exampleMatcher);

        return moMeasurementRepository.findAll(example, pageable);
    }

    private UserMeasurement save(UserMeasurement userMeasurement) {
        final UserMeasurement savedUserMeasurement = userMeasurementRepository.save(userMeasurement);
        log.info("User measurement saved: {} ", savedUserMeasurement);
        return savedUserMeasurement;
    }
}