package com.projetofinal.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetofinal.backend.entities.Funcionario;
import com.projetofinal.backend.services.FuncionarioService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/funcionario")
public class FuncionarioController {

	@Autowired
	private FuncionarioService service;

	@Operation(summary = "Lista de funcionarios", description = "Esse método retorna uma lista de funcionarios")
	@GetMapping
	public ResponseEntity<List<Funcionario>> procuraTodos() {
		List<Funcionario> listaDeFuncionario = service.procuraTodos();
		return ResponseEntity.status(HttpStatus.OK).body(listaDeFuncionario);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Funcionario> procuraPorId(@PathVariable Integer id) {
		Funcionario response = service.procuraPorId(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping
	public ResponseEntity<String> adicionaFuncionario(@RequestBody Funcionario funcionario) {
		String response = service.adicionaFuncionario(funcionario);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<String> editarFuncionario(@PathVariable Integer id, @RequestBody Funcionario funcionario) {
		String response = service.editarFuncionario(id, funcionario);
		return ResponseEntity.status(200).body(response);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluirFuncionario(@PathVariable Integer id) {
		service.excluirFuncionario(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
