package ru.trukhmanov.twochairsbackend.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.trukhmanov.twochairsbackend.dto.game.AnswerStatsDto
import ru.trukhmanov.twochairsbackend.dto.game.QuestionDto
import ru.trukhmanov.twochairsbackend.entity.UserAnswer
import ru.trukhmanov.twochairsbackend.repository.game.DeckQuestionRepository
import ru.trukhmanov.twochairsbackend.repository.game.QuestionRepository
import ru.trukhmanov.twochairsbackend.repository.game.UserAnswerRepository

@Service
class GameService(
    private val deckService: DeckService,
    private val deckQuestionRepository: DeckQuestionRepository,
    private val questionRepository: QuestionRepository,
    private val userAnswerRepository: UserAnswerRepository
) {

    fun nextQuestion(userId: Long, deckId: Long): java.util.Optional<QuestionDto> {
        deckService.assertCanAccessDeck(userId, deckId)

        return questionRepository.findRandomNext(deckId, userId)
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
    fun answer(userId: Long, deckId: Long, questionId: Long, answer: String?): AnswerStatsDto {
        deckService.assertCanAccessDeck(userId, deckId)

        if (!deckQuestionRepository.existsByIdDeckIdAndIdQuestionId(deckId, questionId)) {
            throw IllegalArgumentException("Question is not in this deck")
        }

        if (answer == null || answer.length != 1) {
            throw IllegalArgumentException("Answer must be A or B")
        }
        val c = answer[0]
        if (c != 'A' && c != 'B') {
            throw IllegalArgumentException("Answer must be A or B")
        }

        if (userAnswerRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw IllegalArgumentException("Already answered")
        }

        questionRepository.findById(questionId)
            .orElseThrow { IllegalArgumentException("Question not found") }

        userAnswerRepository.save(
            UserAnswer(
                userId = userId,
                questionId = questionId,
                answer = c
            )
        )

        val pct = userAnswerRepository.calcPercentages(questionId)
        return AnswerStatsDto(
            pctA = pct.pctA ?: 0,
            pctB = pct.pctB ?: 0
        )
    }
}
