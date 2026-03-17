package ru.trukhmanov.twochairsbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto;
import ru.trukhmanov.twochairsbackend.dto.game.deck.*;
import ru.trukhmanov.twochairsbackend.entity.Deck;
import ru.trukhmanov.twochairsbackend.entity.DeckQuestion;
import ru.trukhmanov.twochairsbackend.entity.Question;
import ru.trukhmanov.twochairsbackend.repository.UserPurchaseRepository;
import ru.trukhmanov.twochairsbackend.repository.UserRepository;
import ru.trukhmanov.twochairsbackend.repository.game.DeckQuestionRepository;
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
    private final DeckQuestionRepository deckQuestionRepository;

    public MyDeckService(DeckRepository deckRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository,
                         UserPurchaseRepository purchaseRepository,
                         DeckQuestionRepository deckQuestionRepository) {
        this.deckRepository = deckRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.deckQuestionRepository = deckQuestionRepository;
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

        String a = req.optionA() == null ? null : req.optionA().trim();
        String b = req.optionB() == null ? null : req.optionB().trim();

        if (a == null || a.isBlank()) throw new IllegalArgumentException("optionA required");
        if (b == null || b.isBlank()) throw new IllegalArgumentException("optionB required");
        if (a.equals(b)) throw new IllegalArgumentException("options must differ");

        Question q = questionRepository.save(Question.builder()
                .optionA(a)
                .optionB(b)
                .active(true)
                .build());

        // Создаём связь вопроса с колодой
        deckQuestionRepository.save(DeckQuestion.of(deckId, q.getId()));

        return q.getId();
    }

    @Transactional
    public void addExistingQuestion(long userId, long deckId, long questionId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        if (!Objects.equals(deck.getOwnerUserId(), userId)) throw new IllegalArgumentException("Not your deck");
        if (!"USER".equals(deck.getType())) throw new IllegalArgumentException("Not a user deck");

        questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Question not found"));

        if (deckQuestionRepository.existsByIdDeckIdAndIdQuestionId(deckId, questionId)) {
            throw new IllegalArgumentException("Already added");
        }

        deckQuestionRepository.save(DeckQuestion.of(deckId, questionId));
    }

    public List<QuestionDto> listQuestions(long userId, long deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        if (!Objects.equals(deck.getOwnerUserId(), userId)) throw new IllegalArgumentException("Not your deck");
        if (!"USER".equals(deck.getType())) throw new IllegalArgumentException("Not a user deck");

        return questionRepository.findAllByDeckId(deckId).stream()
                .map(q -> new QuestionDto(q.getId(), deckId, q.getOptionA(), q.getOptionB()))
                .toList();
    }

    @Transactional
    public void removeQuestion(long userId, long deckId, long questionId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow();
        if (!Objects.equals(deck.getOwnerUserId(), userId)) throw new IllegalArgumentException("Not your deck");
        if (!"USER".equals(deck.getType())) throw new IllegalArgumentException("Not a user deck");

        if (!deckQuestionRepository.existsByIdDeckIdAndIdQuestionId(deckId, questionId)) {
            throw new IllegalArgumentException("Question is not in this deck");
        }

        deckQuestionRepository.deleteByIdDeckIdAndIdQuestionId(deckId, questionId);
    }

    public List<MyDeckPickDto> picker(long userId) {
        return deckRepository.findByOwnerUserIdAndTypeOrderByIdDesc(userId, "USER").stream()
                .map(d -> new MyDeckPickDto(d.getId(), d.getTitle()))
                .toList();
    }
}