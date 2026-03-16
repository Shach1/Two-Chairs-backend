package ru.trukhmanov.twochairsbackend.repository.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trukhmanov.twochairsbackend.dto.game.AnswerPercentagesView;
import ru.trukhmanov.twochairsbackend.entity.UserAnswer;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    boolean existsByUserIdAndQuestionId(long userId, long questionId);

    @Query(value = """
        select
          coalesce(round(100.0 * sum(case when answer = 'A' then 1 else 0 end) / nullif(count(*), 0), 0), 0) as pctA,
          coalesce(round(100.0 * sum(case when answer = 'B' then 1 else 0 end) / nullif(count(*), 0), 0), 0) as pctB
        from user_answers
        where question_id = :questionId
        """, nativeQuery = true)
    AnswerPercentagesView calcPercentages(@Param("questionId") long questionId);
}