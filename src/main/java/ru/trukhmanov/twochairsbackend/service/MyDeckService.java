package ru.trukhmanov.twochairsbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.trukhmanov.twochairsbackend.dto.game.deck.CreateDeckRequest;
import ru.trukhmanov.twochairsbackend.dto.game.deck.CreateQuestionRequest;
import ru.trukhmanov.twochairsbackend.dto.game.deck.DeckDto;
import ru.trukhmanov.twochairsbackend.dto.game.deck.UpdateDeckRequest;
import ru.trukhmanov.twochairsbackend.entity.Deck;
import ru.trukhmanov.twochairsbackend.entity.Question;
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository;
import ru.trukhmanov.twochairsbackend.repository.UserRepository;
import ru.trukhmanov.twochairsbackend.repository.game.DeckRepository;
import ru.trukhmanov.twochairsbackend.repository.game.QuestionRepository;

import java.util.List;
import java.util.Objects;

@Service
public class MyDeckService {

    private final DeckRepository deckRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserPurchaseRepository purchaseRepository;

    public MyDeckService(DeckRepository deckRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository,
                         UserPurchaseRepository purchaseRepository) {
        this.deckRepository = deckRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public List<DeckDto> myDecks(long userId) {
        return deckRepository.findByOwnerUserIdAndTypeOrderByIdDesc(userId, "USER").stream()
                .map(d -> new DeckDto(
                        d.getId(),
                        d.getType(),
                        d.getVisibility(),
                        d.getTitle(),
                        d.getDescription(),
                        d.getAgeRating(),
                        d.getPriceRub(),
                        false,
                        d.getOwnerUserId()))
                .toList();
    }

    @Transactional
    public DeckDto createDeck(long userId, CreateDeckRequest req) {
        var user = userRepository.findById(userId).orElseThrow();
        boolean allowed = user.isPremium() || purchaseRepository.hasCreateDecksFeature(userId);
        if (!allowed) throw new IllegalArgumentException("Feature not purchased");

        if (req.title() == null || req.title().isBlank()) throw new IllegalArgumentException("Title is required");

        Deck deck = deckRepository.save(Deck.builder()
                .type("USER")
                .visibility("PRIVATE")
                .title(req.title().trim())
                .description(req.description())
                .ageRating(req.ageRating())
                .priceRub(0)
                .ownerUserId(userId)
                .published(false)
                .build());

        return new DeckDto(deck.getId(), deck.getType(), deck.getVisibility(), deck.getTitle(), deck.getDescription(),
                deck.getAgeRating(), deck.getPriceRub(), false, deck.getOwnerUserId());
    }

    @Transactional
    public DeckDto updateDeck(long userId, long deckId, UpdateDeckRequest req) {
        Deck deck = deckRepository.findById(deckId).orElseThrow();

        if (!Objects.equals(deck.getOwnerUserId(), userId)) {
            throw new IllegalArgumentException("Not your deck");
        }

        if (req.title() != null) {
            if (req.title().isBlank()) throw new IllegalArgumentException("Title cannot be blank");
            deck.setTitle(req.title().trim());
        }
        if (req.description() != null) deck.setDescription(req.description());
        if (req.ageRating() != null) deck.setAgeRating(req.ageRating());

        return new DeckDto(deck.getId(), deck.getType(), deck.getVisibility(), deck.getTitle(), deck.getDescription(),
                deck.getAgeRating(), deck.getPriceRub(), false, deck.getOwnerUserId());
    }

    @Transactional
    public void publish(long userId, long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        if (!Objects.equals(deck.getOwnerUserId(), userId)) throw new IllegalArgumentException("Not your deck");
        if (!"USER".equals(deck.getType())) throw new IllegalArgumentException("Not a user deck");

        deck.setVisibility("PUBLIC");
        deck.setPublished(true);
    }

    @Transactional
    public long addQuestion(long userId, long deckId, CreateQuestionRequest req) {
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        if (!Objects.equals(deck.getOwnerUserId(), userId)) throw new IllegalArgumentException("Not your deck");
        if (!"USER".equals(deck.getType())) throw new IllegalArgumentException("Not a user deck");

        if (req.optionA() == null || req.optionA().isBlank()) throw new IllegalArgumentException("optionA required");
        if (req.optionB() == null || req.optionB().isBlank()) throw new IllegalArgumentException("optionB required");
        if (req.optionA().trim().equals(req.optionB().trim())) throw new IllegalArgumentException("options must differ");

        Question q = questionRepository.save(Question.builder()
                .deckId(deckId)
                .optionA(req.optionA().trim())
                .optionB(req.optionB().trim())
                .active(true)
                .build());

        return q.getId();
    }
}