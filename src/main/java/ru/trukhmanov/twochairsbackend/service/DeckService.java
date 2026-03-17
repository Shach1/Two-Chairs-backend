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
        var user = userRepository.findById(userId).orElseThrow();
        Deck deck = deckRepository.findById(deckId).orElseThrow();

        // 1) Своя колода — всегда доступна (даже не опубликована / PRIVATE)
        if (Objects.equals(deck.getOwnerUserId(), userId)) {
            return;
        }

        // 2) Чужая колода: должна быть опубликована
        if (!deck.isPublished()) {
            throw new IllegalArgumentException("Deck is not published");
        }

        boolean premium = user.isPremium();

        // 3) DEFAULT (системные): доступны всем всегда (при условии published)
        if ("DEFAULT".equals(deck.getType())) {
            return;
        }

        // 4) Чужие USER без премиума — никогда
        if ("USER".equals(deck.getType()) && !premium) {
            throw new IllegalArgumentException("Deck not accessible");
        }

        // 5) Premium: видит публичные USER + все PAID
        if (premium) {
            if ("PAID".equals(deck.getType())) return;
            if ("USER".equals(deck.getType()) && "PUBLIC".equals(deck.getVisibility())) return;
            throw new IllegalArgumentException("Deck not accessible");
        }

        // 6) Non-premium: только PAID
        if ("PAID".equals(deck.getType())) {
            if (purchaseRepository.hasDeck(userId, deckId)) return;
            throw new IllegalArgumentException("Deck not accessible");
        }

        // 7) Всё остальное — запрещено по умолчанию
        throw new IllegalArgumentException("Deck not accessible");
    }
}