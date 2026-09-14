package com.projetofinal.backend.handler;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projetofinal.backend.dtos.StandardErrorDTO;
import com.projetofinal.backend.exceptions.BadRequestException;
import com.projetofinal.backend.exceptions.ConflictException;
import com.projetofinal.backend.exceptions.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandlers {

	private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<StandardErrorDTO> handleNotFound(NotFoundException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Resource Not Found",
				ex.getMessage(),
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<StandardErrorDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Bad Request",
				ex.getMessage(),
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<StandardErrorDTO> handleConflict(ConflictException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Conflict",
				ex.getMessage(),
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<StandardErrorDTO> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		String message = "Database integrity violation. The record may already exist or is referenced by other entities.";
		String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
		if (rootMsg != null) {
			String lower = rootMsg.toLowerCase();
			if (lower.contains("duplicate") || lower.contains("unique")) {
				message = "A record with these unique identifiers already exists in the system.";
			} else if (lower.contains("foreign key") || lower.contains("constraint")) {
				message = "Operation failed because the record is linked to other active records.";
			}
		}
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Data Integrity Violation",
				message,
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Validation Error",
				"Validation failed for one or more fields.",
				request.getRequestURI()
		);
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			err.addFieldError(fe.getField(), fe.getDefaultMessage());
		}
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<StandardErrorDTO> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Malformed Request",
				"Request body is missing, malformed, or contains invalid field types.",
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<StandardErrorDTO> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Unauthorized",
				"Invalid authentication credentials.",
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<StandardErrorDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Forbidden",
				"Access denied. You do not have permission to access this resource.",
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception at URI: {}", request.getRequestURI(), ex);
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		StandardErrorDTO err = new StandardErrorDTO(
				Instant.now(),
				status.value(),
				"Internal Server Error",
				"An unexpected server error occurred. Please contact technical support.",
				request.getRequestURI()
		);
		return ResponseEntity.status(status).body(err);
	}
}
