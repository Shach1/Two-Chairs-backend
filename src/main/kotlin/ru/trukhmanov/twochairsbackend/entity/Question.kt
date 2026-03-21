package ru.trukhmanov.twochairsbackend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "questions")
class Question(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "option_a", nullable = false)
    var optionA: String = "",

    @Column(name = "option_b", nullable = false)
    var optionB: String = "",

    @Column(name = "is_active", nullable = false)
    var active: Boolean = false
)
