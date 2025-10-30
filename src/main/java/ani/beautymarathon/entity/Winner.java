package ani.beautymarathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "winner")
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "mo_measurement_id")
    private MoMeasurement moMeasurement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "average_point", updatable = false)
    private Double averagePoint;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Override
    public String toString() {
        return "Winner of " + moMeasurement.getMonthNumber() +
                ", " + moMeasurement.getYear() +
                ": user " + user + ", " + user.getName() +
                "with averagePoint: " + averagePoint;
    }
}