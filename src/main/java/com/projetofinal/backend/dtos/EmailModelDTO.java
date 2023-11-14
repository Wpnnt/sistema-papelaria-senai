package com.projetofinal.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailModelDTO {
	@NotBlank
	@Email
	private String emailFrom;

	@NotBlank
	@Email
	private String emailTo;

	@NotBlank
	private String tituloEmail;

	@NotBlank
	private String corpoEmail;
}