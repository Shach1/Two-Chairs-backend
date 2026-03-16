package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="deck_id", nullable=false)
    private Long deckId;

    @Column(name="option_a", nullable=false)
    private String optionA;

    @Column(name="option_b", nullable=false)
    private String optionB;

    @Column(name="is_active", nullable=false)
    private boolean active;
}