package ru.trukhmanov.twochairsbackend.repository.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trukhmanov.twochairsbackend.entity.Deck;

import java.util.List;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    // витрина платных колод
    @Query("""
        select d from Deck d
        where d.type = 'PAID'
          and d.published = true
          and d.priceRub > 0
        order by d.id desc
        """)
    List<Deck> findStorePaidDecks();

    @Query("""
        select d from Deck d
        where d.published = true
          and (
                d.priceRub = 0
                or d.ownerUserId = :userId
                or d.id in (
                    select p.deckId from Product p
                    join UserPurchase up on up.productId = p.id
                    where up.userId = :userId
                      and p.type = 'DECK'
                      and p.deckId is not null
                )
          )
        """)
    List<Deck> findAccessibleForNonPremium(@Param("userId") long userId);

    @Query("""
        select d from Deck d
        where d.published = true
          and (
                d.priceRub = 0
                or d.type = 'PAID'
                or d.ownerUserId = :userId
                or (d.type = 'USER' and d.visibility = 'PUBLIC')
          )
        """)
    List<Deck> findAccessibleForPremium(@Param("userId") long userId);

    List<Deck> findByOwnerUserIdAndTypeOrderByIdDesc(long ownerUserId, String type);
}
