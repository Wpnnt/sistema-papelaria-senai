package com.projetofinal.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projetofinal.backend.entities.EmailModel;

@Repository
public interface EmailRepository extends JpaRepository<EmailModel, Long> {
}