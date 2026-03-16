package ru.trukhmanov.twochairsbackend.repository.game;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trukhmanov.twochairsbackend.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long>{

    @Query("""
        select q from Question q
        where q.deckId = :deckId
          and q.active = true
          and not exists (
            select 1 from UserAnswer ua
            where ua.userId = :userId and ua.questionId = q.id
          )
        order by function('random')
        """)
    List<Question> findRandomNext(@Param("deckId") long deckId,
                                  @Param("userId") long userId,
                                  Pageable pageable);
    }