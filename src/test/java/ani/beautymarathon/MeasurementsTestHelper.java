package ani.beautymarathon;

import ani.beautymarathon.entity.ClosedState;
import ani.beautymarathon.entity.MoMeasurement;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.entity.UserMeasurement;
import ani.beautymarathon.entity.WkMeasurement;
import ani.beautymarathon.view.measurement.GetMoMeasurementView;
import ani.beautymarathon.view.measurement.GetUserMeasurementView;
import ani.beautymarathon.view.measurement.GetWkMeasurementView;
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

    public static WkMeasurement createTestWkMeasurement() {
        long id = 1;
        MoMeasurement moMeasurement = createTestMoMeasurement();
        WkMeasurement wkMeasurement = new WkMeasurement();
        wkMeasurement.setId(id);
        wkMeasurement.setMoMeasurement(moMeasurement);
        wkMeasurement.setClosedState(ClosedState.OPEN);
        wkMeasurement.setMeasurementDate(LocalDate.now());
        wkMeasurement.setCommentary("test");
        return wkMeasurement;
    }

    public static UserMeasurement createTestUserMeasurement() {
        long id = 1;
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        User user = UserTestHelper.createTestUser();
        UserMeasurement userMeasurement = new UserMeasurement();
        userMeasurement.setId(id);
        userMeasurement.setWkMeasurement(wkMeasurement);
        userMeasurement.setUser(user);
        userMeasurement.setWaterPoint(5);
        userMeasurement.setStepPoint(7);
        userMeasurement.setSleepPoint(3);
        userMeasurement.setDiaryPoint(2);
        userMeasurement.setAlcoholFreePoint(10);
        userMeasurement.setWeightPoint(0);
        userMeasurement.setWeight(new BigDecimal(65));
        userMeasurement.setCommentary("test measurement");

        return userMeasurement;
    }

    public static GetMoMeasurementView createTestMoMeasurementView(MoMeasurement moMeasurement) {
        return new GetMoMeasurementView(
                moMeasurement.getId(),
                moMeasurement.getClosedState(),
                moMeasurement.getYear(),
                moMeasurement.getMonthNumber()
        );
    }

    public static GetWkMeasurementView createTestWkMeasurementView(WkMeasurement wkMeasurement) {
        MoMeasurement moMeasurement = createTestMoMeasurement();
        GetMoMeasurementView moMeasurementView = createTestMoMeasurementView(moMeasurement);

        return new GetWkMeasurementView(
                wkMeasurement.getId(),
                wkMeasurement.getMeasurementDate(),
                wkMeasurement.getClosedState(),
                wkMeasurement.getCommentary(),
                moMeasurementView
        );

    }

    public static GetUserMeasurementView createTestUserMeasurementView(UserMeasurement userMeasurement){
        User user = UserTestHelper.createTestUser();
        GetUserView userView = UserTestHelper.createTestUserView(user);
        WkMeasurement wkMeasurement = createTestWkMeasurement();
        GetWkMeasurementView wkMeasurementView = createTestWkMeasurementView(wkMeasurement);

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
}