package ru.trukhmanov.twochairsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trukhmanov.twochairsbackend.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByPhoneNumber(String phoneNumber);
}