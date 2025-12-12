package ani.beautymarathon;

import ani.beautymarathon.entity.ClosedState;
import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.user.GetUserView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MeasurementsTestHelper {

    public static MoMeasurement createTestMoMeasurement() {
        long id = 1;
        LocalDate today = LocalDate.now();
        MoMeasurement measurement = new MoMeasurement();
        measurement.setId(id);
        measurement.setMonthNumber(9);
        measurement.setYear(2025);
        measurement.setMoDate(today);
        measurement.setClosedState(ClosedState.CLOSED);
        measurement.setWkMeasurements(List.of());
        return measurement;
    }

    public static User createTestUser() {
        long id = 1;
        User user = new User();
        user.setId(id);
        user.setEmail("test@test.com");
        user.setStartWeight(new BigDecimal("66.3"));
        user.setTargetWeight(new BigDecimal("50.0"));
        user.setDeletedState(DeletedState.NOT_DELETED);
        user.setName("Test name");
        return user;
    }

    public static GetMoMeasurementView createTestMoMeasurementView(MoMeasurement moMeasurement) {
        return new GetMoMeasurementView(
                moMeasurement.getId(),
                moMeasurement.getClosedState(),
                moMeasurement.getYear(),
                moMeasurement.getMonthNumber()
        );
    }

    public static GetUserView createTestUserView(User user) {
        return new GetUserView(
                user.getId(),
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getCreationDate(),
                user.getDeletedState(),
                user.getEmail()
        );
    }
}