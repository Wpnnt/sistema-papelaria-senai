package com.projetofinal.backend.dtos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class StandardErrorDTO {

	private Instant timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
	private List<ValidationErrorDTO> fieldErrors = new ArrayList<>();

	public StandardErrorDTO() {}

	public StandardErrorDTO(Instant timestamp, Integer status, String error, String message, String path) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ValidationErrorDTO> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<ValidationErrorDTO> fieldErrors) {
		this.fieldErrors = fieldErrors != null ? fieldErrors : new ArrayList<>();
	}

	public void addFieldError(String field, String message) {
		this.fieldErrors.add(new ValidationErrorDTO(field, message));
	}
}
