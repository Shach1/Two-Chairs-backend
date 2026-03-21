package ru.trukhmanov.twochairsbackend.service

import org.springframework.stereotype.Service
import ru.trukhmanov.twochairsbackend.dto.game.deck.DeckDto
import ru.trukhmanov.twochairsbackend.entity.Deck
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository
import ru.trukhmanov.twochairsbackend.repository.UserRepository
import ru.trukhmanov.twochairsbackend.repository.game.DeckRepository

@Service
class DeckService(
    private val deckRepository: DeckRepository,
    private val userRepository: UserRepository,
    private val purchaseRepository: UserPurchaseRepository
) {

    fun getAccessibleDecks(userId: Long): List<DeckDto> {
        val premium = userRepository.findById(userId).orElseThrow().premium

        val decks = if (premium) {
            deckRepository.findAccessibleForPremium(userId)
        } else {
            deckRepository.findAccessibleForNonPremium(userId)
        }

        return decks.map { toDto(it, false) }
    }

    fun getStoreDecks(userId: Long): List<DeckDto> {
        val premium = userRepository.findById(userId).orElseThrow().premium
        val decks = deckRepository.findStorePaidDecks()

        return decks.map { deck ->
            val locked = if (premium) {
                false
            } else {
                purchaseRepository.hasDeck(userId, requireNotNull(deck.id))
            }.not()
            toDto(deck, locked)
        }
    }

    private fun toDto(deck: Deck, locked: Boolean): DeckDto {
        return DeckDto(
            id = requireNotNull(deck.id),
            type = deck.type,
            visibility = deck.visibility,
            title = deck.title,
            description = deck.description,
            ageRating = deck.ageRating,
            priceRub = deck.priceRub,
            locked = locked,
            ownerUserId = deck.ownerUserId
        )
    }

    fun assertCanAccessDeck(userId: Long, deckId: Long) {
        val user = userRepository.findById(userId).orElseThrow()
        val deck = deckRepository.findById(deckId).orElseThrow()

        if (deck.ownerUserId == userId) {
            return
        }

        if (!deck.published) {
            throw IllegalArgumentException("Deck is not published")
        }

        val premium = user.premium

        if (deck.type == "DEFAULT") {
            return
        }

        if (deck.type == "USER" && !premium) {
            throw IllegalArgumentException("Deck not accessible")
        }

        if (premium) {
            if (deck.type == "PAID") return
            if (deck.type == "USER" && deck.visibility == "PUBLIC") return
            throw IllegalArgumentException("Deck not accessible")
        }

        if (deck.type == "PAID") {
            if (purchaseRepository.hasDeck(userId, deckId)) return
            throw IllegalArgumentException("Deck not accessible")
        }

        throw IllegalArgumentException("Deck not accessible")
    }
}
