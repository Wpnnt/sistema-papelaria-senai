package com.projetofinal.backend.client;

import com.projetofinal.backend.dtos.StandardErrorDTO;
import com.projetofinal.backend.dtos.ValidationErrorDTO;

public class ApiException extends Exception {

	private final int statusCode;
	private final StandardErrorDTO standardError;

	public ApiException(int statusCode, StandardErrorDTO standardError) {
		super(extractMessage(statusCode, standardError));
		this.statusCode = statusCode;
		this.standardError = standardError;
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
		this.statusCode = -1;
		this.standardError = null;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public StandardErrorDTO getStandardError() {
		return standardError;
	}

	public String getFormattedDetails() {
		if (standardError == null) {
			return getMessage();
		}
		StringBuilder sb = new StringBuilder();
		if (standardError.getMessage() != null && !standardError.getMessage().isBlank()) {
			sb.append(standardError.getMessage()).append("\n");
		}
		if (standardError.getFieldErrors() != null && !standardError.getFieldErrors().isEmpty()) {
			sb.append("\n");
			for (ValidationErrorDTO fe : standardError.getFieldErrors()) {
				sb.append(" • ").append(fe.getField()).append(": ").append(fe.getMessage()).append("\n");
			}
		}
		return sb.toString().trim();
	}

	private static String extractMessage(int statusCode, StandardErrorDTO error) {
		if (error != null && error.getMessage() != null && !error.getMessage().isBlank()) {
			return error.getMessage();
		}
		return "HTTP " + statusCode + " error from server";
	}
}
