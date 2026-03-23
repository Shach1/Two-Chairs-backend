package ru.trukhmanov.twochairsbackend.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto
import ru.trukhmanov.twochairsbackend.dto.game.deck.CreateDeckRequest
import ru.trukhmanov.twochairsbackend.dto.game.deck.CreateQuestionRequest
import ru.trukhmanov.twochairsbackend.dto.game.deck.DeckDto
import ru.trukhmanov.twochairsbackend.dto.game.deck.MyDeckPickDto
import ru.trukhmanov.twochairsbackend.dto.game.deck.UpdateDeckRequest
import ru.trukhmanov.twochairsbackend.entity.Deck
import ru.trukhmanov.twochairsbackend.entity.DeckQuestion
import ru.trukhmanov.twochairsbackend.entity.Question
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository
import ru.trukhmanov.twochairsbackend.repository.UserRepository
import ru.trukhmanov.twochairsbackend.repository.game.DeckQuestionRepository
import ru.trukhmanov.twochairsbackend.repository.game.DeckRepository
import ru.trukhmanov.twochairsbackend.repository.game.QuestionRepository

@Service
class MyDeckService(
    private val deckRepository: DeckRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    private val purchaseRepository: UserPurchaseRepository,
    private val deckQuestionRepository: DeckQuestionRepository
) {

    fun assertCanCreateDeck(userId: Long) {
        val user = userRepository.findById(userId).orElseThrow()
        val allowed = user.premium || purchaseRepository.hasCreateDecksFeature(userId)
        if (!allowed) {
            throw IllegalArgumentException("To create your own decks you need PREMIUM or FEATURE_CREATE_DECKS")
        }
    }

    fun myDecks(userId: Long): List<DeckDto> {
        return deckRepository.findByOwnerUserIdAndTypeOrderByIdDesc(userId, "USER")
            .map { deck ->
                DeckDto(
                    id = requireNotNull(deck.id),
                    type = deck.type,
                    visibility = deck.visibility,
                    title = deck.title,
                    description = deck.description,
                    ageRating = deck.ageRating,
                    priceRub = deck.priceRub,
                    locked = false,
                    ownerUserId = deck.ownerUserId
                )
            }
    }

    @Transactional
    fun createDeck(userId: Long, req: CreateDeckRequest): DeckDto {
        assertCanCreateDeck(userId)

        if (req.title.isBlank()) {
            throw IllegalArgumentException("Title is required")
        }

        val deck = deckRepository.save(
            Deck(
                type = "USER",
                visibility = "PRIVATE",
                title = req.title.trim(),
                description = req.description,
                ageRating = req.ageRating,
                priceRub = 0,
                ownerUserId = userId,
                published = false
            )
        )

        return DeckDto(
            id = requireNotNull(deck.id),
            type = deck.type,
            visibility = deck.visibility,
            title = deck.title,
            description = deck.description,
            ageRating = deck.ageRating,
            priceRub = deck.priceRub,
            locked = false,
            ownerUserId = deck.ownerUserId
        )
    }

    @Transactional
    fun updateDeck(userId: Long, deckId: Long, req: UpdateDeckRequest): DeckDto {
        val deck = deckRepository.findById(deckId).orElseThrow()

        if (deck.ownerUserId != userId) {
            throw IllegalArgumentException("Not your deck")
        }

        req.title?.let { title ->
            if (title.isBlank()) {
                throw IllegalArgumentException("Title cannot be blank")
            }
            deck.title = title.trim()
        }
        req.description?.let { deck.description = it }
        req.ageRating?.let { deck.ageRating = it }

        return DeckDto(
            id = requireNotNull(deck.id),
            type = deck.type,
            visibility = deck.visibility,
            title = deck.title,
            description = deck.description,
            ageRating = deck.ageRating,
            priceRub = deck.priceRub,
            locked = false,
            ownerUserId = deck.ownerUserId
        )
    }

    @Transactional
    fun publish(userId: Long, deckId: Long) {
        val deck = deckRepository.findById(deckId).orElseThrow()
        if (deck.ownerUserId != userId) {
            throw IllegalArgumentException("Not your deck")
        }
        if (deck.type != "USER") {
            throw IllegalArgumentException("Not a user deck")
        }

        deck.visibility = "PUBLIC"
        deck.published = true
    }

    @Transactional
    fun addQuestion(userId: Long, deckId: Long, req: CreateQuestionRequest): Long {
        val deck = deckRepository.findById(deckId).orElseThrow()
        if (deck.ownerUserId != userId) {
            throw IllegalArgumentException("Not your deck")
        }

        val a = req.optionA.trim()
        val b = req.optionB.trim()

        if (a.isBlank()) {
            throw IllegalArgumentException("optionA required")
        }
        if (b.isBlank()) {
            throw IllegalArgumentException("optionB required")
        }
        if (a == b) {
            throw IllegalArgumentException("options must differ")
        }

        val question = questionRepository.save(
            Question(
                optionA = a,
                optionB = b,
                active = true
            )
        )

        val questionId = requireNotNull(question.id)
        deckQuestionRepository.save(DeckQuestion.of(deckId, questionId))

        return questionId
    }

    @Transactional
    fun addExistingQuestion(userId: Long, deckId: Long, questionId: Long) {
        val deck = deckRepository.findById(deckId).orElseThrow()
        if (deck.ownerUserId != userId) {
            throw IllegalArgumentException("Not your deck")
        }
        if (deck.type != "USER") {
            throw IllegalArgumentException("Not a user deck")
        }

        questionRepository.findById(questionId)
            .orElseThrow { IllegalArgumentException("Question not found") }

        if (deckQuestionRepository.existsByIdDeckIdAndIdQuestionId(deckId, questionId)) {
            throw IllegalArgumentException("Already added")
        }

        deckQuestionRepository.save(DeckQuestion.of(deckId, questionId))
    }

    fun listQuestions(userId: Long, deckId: Long): List<QuestionDto> {
        val deck = deckRepository.findById(deckId).orElseThrow()
        if (deck.ownerUserId != userId) {
            throw IllegalArgumentException("Not your deck")
        }
        if (deck.type != "USER") {
            throw IllegalArgumentException("Not a user deck")
        }

        return questionRepository.findAllByDeckId(deckId)
            .map { question ->
                QuestionDto(
                    id = requireNotNull(question.id),
                    deckId = deckId,
                    optionA = question.optionA,
                    optionB = question.optionB
                )
            }
    }

    @Transactional
    fun removeQuestion(userId: Long, deckId: Long, questionId: Long) {
        val deck = deckRepository.findById(deckId).orElseThrow()
        if (deck.ownerUserId != userId) {
            throw IllegalArgumentException("Not your deck")
        }
        if (deck.type != "USER") {
            throw IllegalArgumentException("Not a user deck")
        }

        if (!deckQuestionRepository.existsByIdDeckIdAndIdQuestionId(deckId, questionId)) {
            throw IllegalArgumentException("Question is not in this deck")
        }

        deckQuestionRepository.deleteByIdDeckIdAndIdQuestionId(deckId, questionId)
    }

    fun picker(userId: Long): List<MyDeckPickDto> {
        return deckRepository.findByOwnerUserIdAndTypeOrderByIdDesc(userId, "USER")
            .map { deck ->
                MyDeckPickDto(id = requireNotNull(deck.id), title = deck.title)
            }
    }
}
