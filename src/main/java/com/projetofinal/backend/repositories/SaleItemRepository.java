package com.projetofinal.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetofinal.backend.entities.SaleItem;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
}
