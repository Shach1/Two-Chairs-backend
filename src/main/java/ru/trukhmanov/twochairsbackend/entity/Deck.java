package ru.trukhmanov.twochairsbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="type", nullable=false, length=16)
    private String type; // DEFAULT/PAID/USER

    @Column(name="visibility", nullable=false, length=16)
    private String visibility; // PRIVATE/PUBLIC

    @Column(name="title", nullable=false, length=80)
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="age_rating", nullable=false)
    private int ageRating;

    @Column(name="price_rub", nullable=false)
    private int priceRub;

    @Column(name="owner_user_id")
    private Long ownerUserId;

    @Column(name="is_published", nullable=false)
    private boolean published;
}
