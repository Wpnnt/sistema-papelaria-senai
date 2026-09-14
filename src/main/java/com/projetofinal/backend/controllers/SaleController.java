package com.projetofinal.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetofinal.backend.dtos.SaleRequestDTO;
import com.projetofinal.backend.entities.Sale;
import com.projetofinal.backend.services.SaleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sales")
public class SaleController {

	@Autowired
	private SaleService saleService;

	@PostMapping
	public ResponseEntity<Sale> createSale(@Valid @RequestBody SaleRequestDTO request) {
		Sale created = saleService.createSale(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
	public ResponseEntity<List<Sale>> listAll() {
		return ResponseEntity.ok(saleService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Sale> findById(@PathVariable Long id) {
		return ResponseEntity.ok(saleService.findById(id));
	}
}
