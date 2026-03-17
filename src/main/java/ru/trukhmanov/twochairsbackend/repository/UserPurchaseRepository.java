package ru.trukhmanov.twochairsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trukhmanov.twochairsbackend.entity.UserPurchase;

public interface UserPurchaseRepository extends JpaRepository<UserPurchase, Long> {

    @Query("""
        select (count(up) > 0)
        from UserPurchase up
        join Product p on p.id = up.productId
        where up.userId = :userId
          and p.type = 'DECK'
          and p.deckId = :deckId
        """)
    boolean hasDeck(@Param("userId") long userId, @Param("deckId") long deckId);

    @Query("""
        select (count(up) > 0)
        from UserPurchase up
        join Product p on p.id = up.productId
        where up.userId = :userId
          and p.type = 'FEATURE_CREATE_DECKS'
        """)
    boolean hasCreateDecksFeature(@Param("userId") long userId);

    boolean existsByUserIdAndProductId(long userId, long productId);
}