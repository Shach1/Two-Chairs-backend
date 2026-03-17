package ru.trukhmanov.twochairsbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerPercentagesView;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerStatsDto;
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto;
import ru.trukhmanov.twochairsbackend.entity.Question;
import ru.trukhmanov.twochairsbackend.entity.UserAnswer;
import ru.trukhmanov.twochairsbackend.repository.game.DeckQuestionRepository;
import ru.trukhmanov.twochairsbackend.repository.game.QuestionRepository;
import ru.trukhmanov.twochairsbackend.repository.game.UserAnswerRepository;

import java.util.Optional;

@Service
public class GameService {

    private final DeckService deckService;
    private final DeckQuestionRepository deckQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public GameService(DeckService deckService,
                       DeckQuestionRepository deckQuestionRepository,
                       QuestionRepository questionRepository,
                       UserAnswerRepository userAnswerRepository) {
        this.deckService = deckService;
        this.deckQuestionRepository = deckQuestionRepository;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    public Optional<QuestionDto> nextQuestion(long userId, long deckId) {
        deckService.assertCanAccessDeck(userId, deckId);

        return questionRepository.findRandomNext(deckId, userId)
                .map(q -> new QuestionDto(q.getId(), deckId, q.getOptionA(), q.getOptionB()));
    }

    @Transactional
    public AnswerStatsDto answer(long userId, long deckId, long questionId, String answer) {
        deckService.assertCanAccessDeck(userId, deckId);

        if (!deckQuestionRepository.existsByIdDeckIdAndIdQuestionId(deckId, questionId)) {
            throw new IllegalArgumentException("Question is not in this deck");
        }

        if (answer == null || answer.length() != 1) throw new IllegalArgumentException("Answer must be A or B");
        char c = answer.charAt(0);
        if (c != 'A' && c != 'B') throw new IllegalArgumentException("Answer must be A or B");

        if (userAnswerRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw new IllegalArgumentException("Already answered");
        }

        // убедимся, что question существует (для красивой ошибки)
        questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Question not found"));

        userAnswerRepository.save(UserAnswer.builder()
                .userId(userId)
                .questionId(questionId)
                .answer(c)
                .build());

        AnswerPercentagesView pct = userAnswerRepository.calcPercentages(questionId);
        return new AnswerStatsDto(pct.getPctA(), pct.getPctB());
    }
}