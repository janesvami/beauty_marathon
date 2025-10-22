package ani.beautymarathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "wk_measurement")
public class WkMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "measurement_date")
    private LocalDate measurementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "closed_state")
    private ClosedState closedState = ClosedState.OPEN;


    @Column(name = "commentary")
    private String commentary;
    @ManyToOne
    @JoinColumn(name = "mo_measurement_id")
    private MoMeasurement moMeasurement;

    @OneToMany(mappedBy = "wkMeasurement")
    private List<UserMeasurement> userMeasurements;

    public WkMeasurement(
            Long id,
            LocalDate measurementDate,
            ClosedState closedState,
            String commentary,
            MoMeasurement moMeasurement,
            List<UserMeasurement> userMeasurements
    ) {
        this.id = id;
        this.measurementDate = measurementDate;
        this.closedState = closedState;
        this.commentary = commentary;
        this.moMeasurement = moMeasurement;
        this.userMeasurements = userMeasurements;
    }

    public WkMeasurement() {
    }

    @Override
    public String toString() {
        return "WkMeasurement{" +
                "id=" + id +
                ", measurementDate=" + measurementDate +
                ", closedState=" + closedState +
                ", commentary='" + commentary + '\'' +
                ", moMeasurementId=" + moMeasurement.getId() +
                '}';
    }
}