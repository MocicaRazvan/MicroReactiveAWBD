CREATE TABLE IF NOT EXISTS exercise
(
    id            SERIAL PRIMARY KEY,
    muscle_groups TEXT[]  NOT NULL,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    body          TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    user_likes    BIGINT[]         DEFAULT '{}',
    user_dislikes BIGINT[]         DEFAULT '{}',
    user_id       BIGINT,
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[]           default '{}',
    videos        TEXT[]           default '{}'
);

