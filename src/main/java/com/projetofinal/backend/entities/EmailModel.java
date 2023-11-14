package com.projetofinal.backend.entities;

import java.time.LocalDateTime;

import com.projetofinal.backend.enums.StatusEmail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class EmailModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long emailId;
	private String emailFrom;
	private String emailTo;
	private String tituloEmail;
	@Column(columnDefinition = "TEXT")
	private String corpoEmail;
	private LocalDateTime sendDateEmail;
	private StatusEmail statusEmail;
}