CREATE TABLE IF NOT EXISTS order_custom
(
    id               SERIAL PRIMARY KEY,
    shipping_address TEXT    NOT NULL,
    payed            BOOLEAN NOT NULL DEFAULT FALSE,
    trainings        BIGINT[],
    user_id          BIGINT,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);