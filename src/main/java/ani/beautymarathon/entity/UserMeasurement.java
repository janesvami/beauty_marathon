package ani.beautymarathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_measurement")
public class UserMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "weight_point")
    private Integer weightPoint;

    @Column(name = "sleep_points")
    private Integer sleepPoint;

    @Column(name = "water_points")
    private Integer waterPoint;

    @Column(name = "step_points")
    private Integer stepPoint;

    @Column(name = "diary_points")
    private Integer diaryPoint;

    @Column(name = "alcohol_free_points")
    private Integer alcoholFreePoints;

    @Column(name = "commentary")
    private String commentary;

    @ManyToOne
    @JoinColumn(name = "wk_measurement_id")
    private WkMeasurement wkMeasurement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_point", updatable = false, insertable = false)
    private Integer totalPoint;

    @Override
    public String toString() {
        return "Measurement per week: " + wkMeasurement +
                "\nUser " + user +
                ":\nweight: " + weight +
                "\nweight points: " + weightPoint +
                "\nsleep points: " + sleepPoint +
                "\nwater points: " + waterPoint +
                "\nstep points: " + stepPoint +
                "\ndiary points: " + diaryPoint +
                "\nalocohol-free points: " + alcoholFreePoints +
                "\n\nCommentary: " + commentary
                ;
    }
}
