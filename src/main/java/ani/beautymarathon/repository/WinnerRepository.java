package ani.beautymarathon.repository;

import ani.beautymarathon.entity.Winner;
import ani.beautymarathon.view.user.UserMaxAverageView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, Long> {

    @Query(value = """
        WITH user_avg AS (
          SELECT
              um.user_id,
              AVG(um.total_point) AS average_score
          FROM user_measurement um
          JOIN public.wk_measurement wm ON wm.id = um.wk_measurement_id
          JOIN public.mo_measurement mm ON mm.id = wm.mo_measurement_id
          WHERE mm.id = :moId
          GROUP BY um.user_id
        )
        SELECT user_id AS userId, average_score AS maxAverageTotal
        FROM user_avg
        WHERE average_score = (SELECT MAX(average_score) FROM user_avg)
        """,
            nativeQuery = true)
    List<UserMaxAverageView> findUsersWithMaxAverage(@Param("moId") Long moId);
}
