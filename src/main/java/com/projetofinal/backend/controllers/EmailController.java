package com.projetofinal.backend.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetofinal.backend.dtos.EmailDTO;
import com.projetofinal.backend.entities.EmailModel;
import com.projetofinal.backend.services.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/send-email")
@Tag(name = "Email", description = "Email notification endpoints")
public class EmailController {

	@Autowired
	private EmailService service;

	@Operation(summary = "Send email", description = "Sends an email message and stores the delivery status")
	@PostMapping
	public ResponseEntity<EmailModel> sendEmail(@RequestBody @Valid EmailDTO dto) {
		EmailModel email = new EmailModel();
		BeanUtils.copyProperties(dto, email);
		EmailModel saved = service.sendEmail(email);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
}
