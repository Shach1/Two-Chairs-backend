-- Связующая таблица: вопрос - колоде
CREATE TABLE deck_questions (
                                deck_id     BIGINT NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
                                question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
                                PRIMARY KEY (deck_id, question_id)
);

CREATE INDEX idx_deck_questions_deck_id ON deck_questions(deck_id);
CREATE INDEX idx_deck_questions_question_id ON deck_questions(question_id);

-- Перенос связи из questions.deck_id в deck_questions
INSERT INTO deck_questions(deck_id, question_id)
SELECT q.deck_id, q.id
FROM questions q
WHERE q.deck_id IS NOT NULL
ON CONFLICT DO NOTHING;

-- Удаление внешнего ключа и колонку deck_id из questions
DO $$
    DECLARE
        fk_name text;
    BEGIN
        SELECT conname INTO fk_name
        FROM pg_constraint
        WHERE conrelid = 'questions'::regclass
          AND contype = 'f'
        LIMIT 1;

        IF fk_name IS NOT NULL THEN
            EXECUTE format('ALTER TABLE questions DROP CONSTRAINT %I', fk_name);
        END IF;
    END$$;

ALTER TABLE questions DROP COLUMN deck_id;

-- Старый индекс по questions(deck_id) больше не нужен
DROP INDEX IF EXISTS idx_questions_deck_id;
