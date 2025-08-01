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
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "closed_state")
    private ClosedState closedState;

    @Column(name = "year", updatable = false)
    private Integer year;

    @OneToMany(mappedBy = "moMeasurement")
    private List<WkMeasurement> wkMeasurements;

    public MoMeasurement(Long id, LocalDate date, ClosedState closedState,
                         Integer year, List<WkMeasurement> wkMeasurements) {
        this.id = id;
        this.date = date;
        this.closedState = closedState;
        this.year = year;
        this.wkMeasurements = wkMeasurements;
    }

    public MoMeasurement() {

    }
}


