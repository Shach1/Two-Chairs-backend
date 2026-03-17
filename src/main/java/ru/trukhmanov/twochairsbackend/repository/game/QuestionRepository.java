package ru.trukhmanov.twochairsbackend.repository.game;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trukhmanov.twochairsbackend.entity.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>{

    @Query(value = """
        select q.*
        from questions q
        join deck_questions dq on dq.question_id = q.id
        where dq.deck_id = :deckId
          and q.is_active = true
          and not exists (
              select 1 from user_answers ua
              where ua.user_id = :userId and ua.question_id = q.id
          )
        order by random()
        limit 1
        """, nativeQuery = true)
    Optional<Question> findRandomNext(@Param("deckId") long deckId, @Param("userId") long userId);
    }