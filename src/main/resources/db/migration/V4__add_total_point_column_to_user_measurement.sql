ALTER TABLE user_measurement
    ADD column total_point INT GENERATED ALWAYS
        AS (weight_point + sleep_points + water_points + step_points + diary_points + alcohol_free_points) STORED;