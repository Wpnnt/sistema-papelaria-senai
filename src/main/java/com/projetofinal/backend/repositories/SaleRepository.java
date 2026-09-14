package com.projetofinal.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetofinal.backend.entities.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
}
