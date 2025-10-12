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
import ani.beautymarathon.repository.WkMeasurementRepository;
import ani.beautymarathon.view.measurement.CreateUserMeasurementView;
import ani.beautymarathon.view.measurement.filter.register.MoMeasurementFilter;
import ani.beautymarathon.view.measurement.filter.register.UserMeasurementFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class MeasurementService {
    private final MoMeasurementRepository moMeasurementRepository;
    private final WkMeasurementRepository wkMeasurementRepository;
    private final UserMeasurementRepository userMeasurementRepository;
    private final UserRepository userRepository;

    public MeasurementService(MoMeasurementRepository moMeasurementRepository,
                              WkMeasurementRepository wkMeasurementRepository,
                              UserMeasurementRepository userMeasurementRepository, UserRepository userRepository) {
        this.moMeasurementRepository = moMeasurementRepository;
        this.wkMeasurementRepository = wkMeasurementRepository;
        this.userMeasurementRepository = userMeasurementRepository;
        this.userRepository = userRepository;
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
