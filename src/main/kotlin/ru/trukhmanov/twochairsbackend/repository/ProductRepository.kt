package ru.trukhmanov.twochairsbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.trukhmanov.twochairsbackend.entity.Product

interface ProductRepository : JpaRepository<Product, Long> {
    fun findByActiveTrueOrderByIdAsc(): List<Product>
}
