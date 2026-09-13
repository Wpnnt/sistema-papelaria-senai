package com.projetofinal.backend.entities;

import java.time.LocalDateTime;

import com.projetofinal.backend.enums.EmailStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "emails")
public class EmailModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String emailFrom;
	private String emailTo;
	private String subject;

	@Column(columnDefinition = "TEXT")
	private String body;

	private LocalDateTime sentAt;

	@Enumerated(EnumType.STRING)
	private EmailStatus status;
}