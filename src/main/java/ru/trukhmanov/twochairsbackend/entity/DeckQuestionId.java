package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DeckQuestionId implements Serializable {

    @Column(name="deck_id")
    private Long deckId;

    @Column(name="question_id")
    private Long questionId;
}