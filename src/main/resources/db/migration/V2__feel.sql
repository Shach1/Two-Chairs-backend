-- Базовая колода
INSERT INTO decks(type, visibility, title, description, age_rating, price_rub, owner_user_id, is_published)
VALUES ('DEFAULT', 'PUBLIC', 'Базовая колода', 'Базовые вопросы для игры', 6, 0, NULL, TRUE);

-- Платной колоды
INSERT INTO decks(type, visibility, title, description, age_rating, price_rub, owner_user_id, is_published)
VALUES ('PAID', 'PUBLIC', 'Пикантная', '18+ вопросы', 18, 99, NULL, TRUE);

-- Вопросы для базовой колоды
INSERT INTO questions(deck_id, option_a, option_b)
SELECT d.id, 'Пицца', 'Суши'
FROM decks d
WHERE d.type='DEFAULT' AND d.title='Базовая колода'
LIMIT 1;

INSERT INTO questions(deck_id, option_a, option_b)
SELECT d.id, 'Горы', 'Море'
FROM decks d
WHERE d.type='DEFAULT' AND d.title='Базовая колода'
LIMIT 1;

-- Вопросы для платной колоды
INSERT INTO questions(deck_id, option_a, option_b)
SELECT d.id, 'Свидание дома', 'Свидание в ресторане'
FROM decks d
WHERE d.type='PAID' AND d.title='Пикантная'
LIMIT 1;

-- Продукт на платную колоду
INSERT INTO products(type, title, price_rub, deck_id)
SELECT 'DECK', 'Колода: Пикантная', 99, d.id
FROM decks d
WHERE d.type='PAID' AND d.title='Пикантная'
LIMIT 1;

-- Продукт: возможность создавать свои колоды
INSERT INTO products(type, title, price_rub, deck_id)
VALUES ('FEATURE_CREATE_DECKS', 'Создание своих колод', 149, NULL);

-- Продукт: премиум
INSERT INTO products(type, title, price_rub, deck_id)
VALUES ('PREMIUM', 'Премиум', 399, NULL);