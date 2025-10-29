ALTER TABLE user_profile
    ADD column email VARCHAR(255);
CREATE UNIQUE INDEX index_email
    ON user_profile (email)
    WHERE email IS NOT NULL;