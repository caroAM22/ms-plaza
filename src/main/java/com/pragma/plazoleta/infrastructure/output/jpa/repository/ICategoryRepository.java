package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    Optional<CategoryEntity> findByName(String name);
} 