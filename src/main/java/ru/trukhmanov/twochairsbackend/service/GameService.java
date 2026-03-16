package ru.trukhmanov.twochairsbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerPercentagesView;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerStatsDto;
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto;
import ru.trukhmanov.twochairsbackend.entity.Question;
import ru.trukhmanov.twochairsbackend.entity.UserAnswer;
import ru.trukhmanov.twochairsbackend.repository.game.QuestionRepository;
import ru.trukhmanov.twochairsbackend.repository.game.UserAnswerRepository;

import java.util.Optional;

@Service
public class GameService {

    private final DeckService deckService;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public GameService(DeckService deckService,
                       QuestionRepository questionRepository,
                       UserAnswerRepository userAnswerRepository) {
        this.deckService = deckService;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    public Optional<QuestionDto> nextQuestion(long userId, long deckId) {
        deckService.assertCanAccessDeck(userId, deckId);

        var list = questionRepository.findRandomNext(deckId, userId, PageRequest.of(0, 1));
        return list.stream().findFirst().map(q -> new QuestionDto(
                q.getId(),
                q.getDeckId(),
                q.getOptionA(),
                q.getOptionB()));
    }

    @Transactional
    public AnswerStatsDto answer(long userId, long questionId, String answer) {
        if (answer == null || answer.length() != 1) {
            throw new IllegalArgumentException("Answer must be A or B");
        }
        char c = answer.charAt(0);
        if (c != 'A' && c != 'B') {
            throw new IllegalArgumentException("Answer must be A or B");
        }

        Question q = questionRepository.findById(questionId).orElseThrow();
        deckService.assertCanAccessDeck(userId, q.getDeckId());

        if (userAnswerRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw new IllegalArgumentException("Already answered");
        }

        userAnswerRepository.save(UserAnswer.builder()
                .userId(userId)
                .questionId(questionId)
                .answer(c)
                .build());

        AnswerPercentagesView pct = userAnswerRepository.calcPercentages(questionId);
        return new AnswerStatsDto(pct.getPctA(), pct.getPctB());
    }
}