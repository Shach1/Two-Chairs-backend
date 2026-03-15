package ru.trukhmanov.twochairsbackend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trukhmanov.twochairsbackend.entity.AuthSmsCode;

public interface AuthSmsCodeRepository extends JpaRepository<AuthSmsCode, Long> {

    @Query("""
        select c from AuthSmsCode c
        where c.phoneNumber = :phone and c.consumedAt is null
        order by c.createdAt desc
        limit 1
        """)
    Optional<AuthSmsCode> findLatestActiveByPhone(@Param("phone") String phone);
}