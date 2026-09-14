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

import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Product and inventory management endpoints")
public class ProductController {

	@Autowired
	private ProductService service;

	@Operation(summary = "List all products", description = "Returns a list of all products in stock")
	@GetMapping
	public ResponseEntity<List<Product>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@Operation(summary = "Get product by ID", description = "Returns details of a specific product")
	@GetMapping("/{id}")
	public ResponseEntity<Product> findById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@Operation(summary = "Create product", description = "Registers a new product in inventory")
	@PostMapping
	public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
		Product created = service.create(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@Operation(summary = "Update product", description = "Updates an existing product's data")
	@PutMapping("/{id}")
	public ResponseEntity<Product> update(@PathVariable Integer id, @Valid @RequestBody Product product) {
		Product updated = service.update(id, product);
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "Delete product", description = "Removes a product from inventory by ID")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
