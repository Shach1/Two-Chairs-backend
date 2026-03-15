-- Пользователи
CREATE TABLE users (
                       id              BIGSERIAL PRIMARY KEY,
                       phone_number    VARCHAR(20) NOT NULL UNIQUE,  -- +79991234567
                       display_name    VARCHAR(64),
                       is_premium      BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Коды для имитации SMS логина/регистрации
CREATE TABLE auth_sms_codes (
                                id              BIGSERIAL PRIMARY KEY,
                                phone_number    VARCHAR(20) NOT NULL,
                                code            VARCHAR(10) NOT NULL,
                                created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                                expires_at      TIMESTAMPTZ NOT NULL,
                                attempts        INT NOT NULL DEFAULT 0,
                                consumed_at     TIMESTAMPTZ,
                                CONSTRAINT chk_attempts_non_negative CHECK (attempts >= 0)
);

-- Колоды
-- type:
--   DEFAULT  = базовая (общая) колода
--   PAID     = покупаемая колода
--   USER     = пользовательская колода
--
-- visibility:
--   PRIVATE  = видит только владелец
--   PUBLIC   = видят все (публичный доступ)
CREATE TABLE decks (
                       id              BIGSERIAL PRIMARY KEY,
                       type            VARCHAR(16) NOT NULL,
                       visibility      VARCHAR(16) NOT NULL DEFAULT 'PRIVATE',
                       title           VARCHAR(80) NOT NULL,
                       description     TEXT,
                       age_rating      INT NOT NULL DEFAULT 0,
                       price_rub       INT NOT NULL DEFAULT 0,
                       owner_user_id   BIGINT REFERENCES users(id) ON DELETE SET NULL,
                       is_published    BOOLEAN NOT NULL DEFAULT TRUE,

                       CONSTRAINT chk_deck_type CHECK (type IN ('DEFAULT','PAID','USER')),
                       CONSTRAINT chk_deck_visibility CHECK (visibility IN ('PRIVATE','PUBLIC')),
                       CONSTRAINT chk_price_non_negative CHECK (price_rub >= 0),
                       CONSTRAINT chk_age_rating CHECK (age_rating IN (0, 6, 12, 16, 18))
);

-- Вопросы
CREATE TABLE questions (
                           id              BIGSERIAL PRIMARY KEY,
                           deck_id         BIGINT NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
                           option_a        TEXT NOT NULL,
                           option_b        TEXT NOT NULL,
                           is_active       BOOLEAN NOT NULL DEFAULT TRUE,

                           CONSTRAINT chk_option_not_equal CHECK (option_a <> option_b)
);

-- Ответы пользователей
CREATE TABLE user_answers (
                              id              BIGSERIAL PRIMARY KEY,
                              user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              question_id     BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
                              answer          CHAR(1) NOT NULL,

                              CONSTRAINT chk_answer CHECK (answer IN ('A','B')),
                              CONSTRAINT uq_user_question UNIQUE (user_id, question_id)
);

-- Продукты
-- type:
--  DECK = покупка колоды
--  FEATURE_CREATE_DECKS = покупка возможности создавать свои колоды
--  PREMIUM = премиум
CREATE TABLE products (
                          id              BIGSERIAL PRIMARY KEY,
                          type            VARCHAR(32) NOT NULL,
                          title           VARCHAR(80) NOT NULL,
                          price_rub       INT NOT NULL,
                          deck_id         BIGINT REFERENCES decks(id) ON DELETE SET NULL,
                          is_active       BOOLEAN NOT NULL DEFAULT TRUE,

                          CONSTRAINT chk_product_type CHECK (type IN ('DECK','FEATURE_CREATE_DECKS','PREMIUM')),
                          CONSTRAINT chk_product_price CHECK (price_rub >= 0)
);

-- Покупки пользователя
CREATE TABLE user_purchases (
                                id              BIGSERIAL PRIMARY KEY,
                                user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                product_id      BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
                                purchased_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

                                CONSTRAINT uq_user_product UNIQUE (user_id, product_id)
);


-- Для поиска последнего SMS-кода по номеру (вход/регистрация)
CREATE INDEX idx_auth_sms_codes_phone_created_at
    ON auth_sms_codes (phone_number, created_at DESC);

-- Для выдачи вопросов конкретной колоды
CREATE INDEX idx_questions_deck_id
    ON questions (deck_id);

-- Для подсчёта статистики по вопросу (группировка по answer)
CREATE INDEX idx_user_answers_question_id
    ON user_answers (question_id);

-- Для списка колод (например: PAID / DEFAULT / USER)
CREATE INDEX idx_decks_type
    ON decks (type);

-- Для витрины продуктов (премиум/фичи/колоды)
CREATE INDEX idx_products_type
    ON products (type);