--liquibase formatted sql

--changeset fa1ry7a1l:1
CREATE TABLE IF NOT EXISTS telegram_chat
(
    id            BIGINT ,
    registered_at timestamp with time zone NOT NULL,
    PRIMARY KEY (id)
);

--changeset fa1ry7a1l:2
CREATE TABLE IF NOT EXISTS link
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    url             VARCHAR(256) UNIQUE NOT NULL,
    description     VARCHAR(256) NOT NULL,
    updated_at timestamp with time zone,
    PRIMARY KEY (id)
);

--changeset fa1ry7a1l:3
CREATE TABLE IF NOT EXISTS telegram_chat_link
(
    chat_id BIGINT REFERENCES telegram_chat (id) ON DELETE CASCADE,
    link_id BIGINT REFERENCES link (id) ON DELETE CASCADE,
    PRIMARY KEY (chat_id, link_id)
)
