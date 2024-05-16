CREATE TABLE IF NOT EXISTS training
(
    id            SERIAL PRIMARY KEY,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    body          TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    user_likes    BIGINT[]         DEFAULT '{}',
    user_dislikes BIGINT[]         DEFAULT '{}',
    user_id       BIGINT,
    price         DECIMAL,
    exercises     BIGINT[],
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[]           default '{}'
);

