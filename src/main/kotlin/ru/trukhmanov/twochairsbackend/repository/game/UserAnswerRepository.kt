package ru.trukhmanov.twochairsbackend.repository.game

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.trukhmanov.twochairsbackend.dto.game.AnswerPercentagesView
import ru.trukhmanov.twochairsbackend.entity.UserAnswer

interface UserAnswerRepository : JpaRepository<UserAnswer, Long> {

    fun existsByUserIdAndQuestionId(userId: Long, questionId: Long): Boolean

    @Query(
        value = """
        select
          coalesce(round(100.0 * sum(case when answer = 'A' then 1 else 0 end) / nullif(count(*), 0), 0), 0) as pctA,
          coalesce(round(100.0 * sum(case when answer = 'B' then 1 else 0 end) / nullif(count(*), 0), 0), 0) as pctB
        from user_answers
        where question_id = :questionId
        """,
        nativeQuery = true
    )
    fun calcPercentages(@Param("questionId") questionId: Long): AnswerPercentagesView
}
