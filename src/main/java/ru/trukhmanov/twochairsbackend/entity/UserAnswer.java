package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user_answers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="question_id", nullable=false)
    private Long questionId;

    @Column(name="answer", nullable=false)
    private Character answer; // "A" or "B"
}