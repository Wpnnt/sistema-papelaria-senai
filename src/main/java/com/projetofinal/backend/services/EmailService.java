package com.projetofinal.backend.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.EmailModel;
import com.projetofinal.backend.enums.EmailStatus;
import com.projetofinal.backend.repositories.EmailRepository;

import jakarta.transaction.Transactional;

@Service
public class EmailService {

	@Autowired
	private EmailRepository repository;

	@Autowired
	private JavaMailSender emailSender;

	@Transactional
	public EmailModel sendEmail(EmailModel email) {
		email.setSentAt(LocalDateTime.now());
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(email.getEmailFrom());
			message.setTo(email.getEmailTo());
			message.setSubject(email.getSubject());
			message.setText(email.getBody());
			emailSender.send(message);

			email.setStatus(EmailStatus.SENT);
		} catch (MailException e) {
			email.setStatus(EmailStatus.ERROR);
		}
		return repository.save(email);
	}
}
