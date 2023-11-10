package com.projetofinal.backend.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.projetofinal.backend.exceptions.NotFoundException;

@ControllerAdvice
public class ExceptionHandlers {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	@ExceptionHandler(NotFoundException.class)
	public String usuarioNaoEncontrado(NotFoundException ex) {
		return ex.getMessage();
	}
}
