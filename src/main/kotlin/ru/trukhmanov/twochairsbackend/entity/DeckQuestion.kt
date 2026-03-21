package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "deck_questions")
class DeckQuestion(
    @EmbeddedId
    var id: DeckQuestionId = DeckQuestionId()
) {
    companion object {
        fun of(deckId: Long, questionId: Long): DeckQuestion {
            return DeckQuestion(DeckQuestionId(deckId, questionId))
        }
    }
}
