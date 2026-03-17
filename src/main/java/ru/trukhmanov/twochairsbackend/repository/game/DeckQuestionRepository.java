package ru.trukhmanov.twochairsbackend.repository.game;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trukhmanov.twochairsbackend.entity.DeckQuestion;
import ru.trukhmanov.twochairsbackend.entity.DeckQuestionId;

public interface DeckQuestionRepository extends JpaRepository<DeckQuestion, DeckQuestionId> {
    boolean existsByIdDeckIdAndIdQuestionId(long deckId, long questionId);
}