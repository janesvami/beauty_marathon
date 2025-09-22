package ani.beautymarathon.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "mo_measurement")
public class MoMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mo_date")
    private LocalDate moDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "closed_state")
    private ClosedState closedState = ClosedState.OPEN;

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "year", updatable = false, insertable = false)
    private Integer year;

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "month_number", updatable = false, insertable = false)
    private Integer monthNumber;

    @OneToMany(mappedBy = "moMeasurement")
    private List<WkMeasurement> wkMeasurements;

    public MoMeasurement() {

    }

    @Override
    public String toString() {
        return "MoMeasurement{" +
                "id=" + id +
                ", moDate=" + moDate +
                ", closedState=" + closedState +
                ", year=" + year +
                ", monthNumber=" + monthNumber +
                ", wkMeasurements=" + wkMeasurements +
                '}';
    }
}


