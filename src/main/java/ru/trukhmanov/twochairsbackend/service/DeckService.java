package ru.trukhmanov.twochairsbackend.service;

import org.springframework.stereotype.Service;
import ru.trukhmanov.twochairsbackend.dto.game.deck.DeckDto;
import ru.trukhmanov.twochairsbackend.entity.Deck;
import ru.trukhmanov.twochairsbackend.repository.game.DeckRepository;
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository;
import ru.trukhmanov.twochairsbackend.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final UserPurchaseRepository purchaseRepository;

    public DeckService(DeckRepository deckRepository,
                       UserRepository userRepository,
                       UserPurchaseRepository purchaseRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public List<DeckDto> getAccessibleDecks(long userId) {
        boolean premium = userRepository.findById(userId).orElseThrow().isPremium();

        List<Deck> decks = premium
                ? deckRepository.findAccessibleForPremium(userId)
                : deckRepository.findAccessibleForNonPremium(userId);

        return decks.stream()
                .map(d -> toDto(d, false))
                .toList();
    }

    public List<DeckDto> getStoreDecks(long userId) {
        boolean premium = userRepository.findById(userId).orElseThrow().isPremium();
        List<Deck> decks = deckRepository.findStorePaidDecks();

        return decks.stream().map(d -> {
            boolean locked;
            if (premium) locked = false;
            else locked = !purchaseRepository.hasDeck(userId, d.getId());
            return toDto(d, locked);
        }).toList();
    }

    private DeckDto toDto(Deck d, boolean locked) {
        return new DeckDto(
                d.getId(),
                d.getType(),
                d.getVisibility(),
                d.getTitle(),
                d.getDescription(),
                d.getAgeRating(),
                d.getPriceRub(),
                locked,
                d.getOwnerUserId()
        );
    }

    public void assertCanAccessDeck(long userId, long deckId) {
        boolean premium = userRepository.findById(userId).orElseThrow().isPremium();
        Deck deck = deckRepository.findById(deckId).orElseThrow();

        if (!deck.isPublished()) {
            throw new IllegalArgumentException("Deck is not published");
        }

        // свои колоды доступны всегда
        if (Objects.equals(deck.getOwnerUserId(), userId)) return;

        // бесплатные доступны всем
        if (deck.getPriceRub() == 0) return;

        if (premium) {
            // премиум: PAID доступны, USER публичные доступны
            if ("PAID".equals(deck.getType())) return;
            if ("USER".equals(deck.getType()) && "PUBLIC".equals(deck.getVisibility())) return;
        } else {
            // без премиума: только купленные платные колоды
            boolean has = purchaseRepository.hasDeck(userId, deckId);
            if (has) return;
        }
        throw new IllegalArgumentException("Deck not accessible");
    }
}