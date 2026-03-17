package ru.trukhmanov.twochairsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trukhmanov.twochairsbackend.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByIdAsc();
}