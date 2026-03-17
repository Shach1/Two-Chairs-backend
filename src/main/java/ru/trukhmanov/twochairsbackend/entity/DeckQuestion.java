package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="deck_questions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DeckQuestion {

    @EmbeddedId
    private DeckQuestionId id;

    public static DeckQuestion of(long deckId, long questionId) {
        return DeckQuestion.builder()
                .id(new DeckQuestionId(deckId, questionId))
                .build();
    }
}