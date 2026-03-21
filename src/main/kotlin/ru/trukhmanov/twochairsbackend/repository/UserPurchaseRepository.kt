package ru.trukhmanov.twochairsbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.trukhmanov.twochairsbackend.entity.UserPurchase

interface UserPurchaseRepository : JpaRepository<UserPurchase, Long> {

    @Query(
        """
        select (count(up) > 0)
        from UserPurchase up
        join Product p on p.id = up.productId
        where up.userId = :userId
          and p.type = 'DECK'
          and p.deckId = :deckId
        """
    )
    fun hasDeck(@Param("userId") userId: Long, @Param("deckId") deckId: Long): Boolean

    @Query(
        """
        select (count(up) > 0)
        from UserPurchase up
        join Product p on p.id = up.productId
        where up.userId = :userId
          and p.type = 'FEATURE_CREATE_DECKS'
        """
    )
    fun hasCreateDecksFeature(@Param("userId") userId: Long): Boolean

    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean
}
