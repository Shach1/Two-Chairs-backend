package ru.trukhmanov.twochairsbackend.repository.game

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.trukhmanov.twochairsbackend.entity.Deck

interface DeckRepository : JpaRepository<Deck, Long> {

    @Query(
        """
        select d from Deck d
        where d.type = 'PAID'
          and d.published = true
          and d.priceRub > 0
        order by d.id desc
        """
    )
    fun findStorePaidDecks(): List<Deck>

    @Query(
        """
        select d from Deck d
        where
          (
            d.ownerUserId = :userId
          )
          or
          (
            d.published = true
            and (
              d.type = 'DEFAULT'
              or d.id in (
                  select p.deckId from Product p
                  join UserPurchase up on up.productId = p.id
                  where up.userId = :userId
                    and p.type = 'DECK'
                    and p.deckId is not null
              )
            )
          )
        """
    )
    fun findAccessibleForNonPremium(@Param("userId") userId: Long): List<Deck>

    @Query(
        """
        select d from Deck d
        where d.published = true
          and (
                d.priceRub = 0
                or d.type = 'PAID'
                or d.ownerUserId = :userId
                or (d.type = 'USER' and d.visibility = 'PUBLIC')
          )
        """
    )
    fun findAccessibleForPremium(@Param("userId") userId: Long): List<Deck>

    fun findByOwnerUserIdAndTypeOrderByIdDesc(ownerUserId: Long, type: String): List<Deck>
}
