package ani.beautymarathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "user_profile")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_weight")
    private BigDecimal startWeight;

    @Column(name = "target_weight")
    private BigDecimal targetWeight;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "deleted_state")
    private DeletedState deletedState;

    @Column(name = "email")
    private String email;

    public User() {
    }

    public User(
            String name,
            BigDecimal startWeight,
            BigDecimal targetWeight,
            LocalDate creationDate,
            String email
    ) {
        this.name = name;
        this.startWeight = startWeight;
        this.targetWeight = targetWeight;
        this.creationDate = creationDate;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User " + id + ": " + name + ", " + email + ", start weight: "
                + startWeight + ", target weight: " + targetWeight + "\n";
    }
}
