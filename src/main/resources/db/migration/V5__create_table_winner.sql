CREATE TABLE IF NOT EXISTS winner
(
    id                BIGSERIAL PRIMARY KEY,
    mo_measurement_id BIGINT NOT NULL
        CONSTRAINT user_measurement_user_profile_fk
            REFERENCES user_profile (id)
            ON DELETE CASCADE,
    user_id           BIGINT NOT NULL
        CONSTRAINT winner_user_profile_fk
            REFERENCES user_profile (id),
    average_point     NUMERIC(3,1) NOT NULL,
    creation_date DATE DEFAULT NOW()
);