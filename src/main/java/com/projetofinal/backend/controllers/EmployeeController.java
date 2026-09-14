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

import com.projetofinal.backend.entities.Employee;
import com.projetofinal.backend.services.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeeController {

	@Autowired
	private EmployeeService service;

	@Operation(summary = "List all employees", description = "Returns a list of all registered employees")
	@GetMapping
	public ResponseEntity<List<Employee>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@Operation(summary = "Get employee by ID", description = "Returns details of a specific employee")
	@GetMapping("/{id}")
	public ResponseEntity<Employee> findById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@Operation(summary = "Create employee", description = "Registers a new employee")
	@PostMapping
	public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
		Employee created = service.create(employee);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@Operation(summary = "Update employee", description = "Updates an existing employee's information")
	@PutMapping("/{id}")
	public ResponseEntity<Employee> update(@PathVariable Integer id, @Valid @RequestBody Employee employee) {
		Employee updated = service.update(id, employee);
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "Delete employee", description = "Removes an employee by ID")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
