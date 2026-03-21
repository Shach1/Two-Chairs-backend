package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class DeckQuestionId(
    @Column(name = "deck_id")
    var deckId: Long? = null,

    @Column(name = "question_id")
    var questionId: Long? = null
) : Serializable
