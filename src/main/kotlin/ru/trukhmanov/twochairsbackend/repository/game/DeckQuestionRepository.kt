package ru.trukhmanov.twochairsbackend.repository.game

import org.springframework.data.jpa.repository.JpaRepository
import ru.trukhmanov.twochairsbackend.entity.DeckQuestion
import ru.trukhmanov.twochairsbackend.entity.DeckQuestionId

interface DeckQuestionRepository : JpaRepository<DeckQuestion, DeckQuestionId> {
    fun existsByIdDeckIdAndIdQuestionId(deckId: Long, questionId: Long): Boolean

    fun deleteByIdDeckIdAndIdQuestionId(deckId: Long, questionId: Long)
}
