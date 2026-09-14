package com.projetofinal.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.Employee;
import com.projetofinal.backend.exceptions.BadRequestException;
import com.projetofinal.backend.exceptions.ConflictException;
import com.projetofinal.backend.exceptions.NotFoundException;
import com.projetofinal.backend.repositories.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Employee> findAll() {
		return repository.findAll();
	}

	public Employee findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
	}

	public Employee create(Employee employee) {
		if (employee.getPassword() == null || employee.getPassword().isBlank()) {
			throw new BadRequestException("Employee password is required.");
		}
		if (employee.getEmail() != null && repository.findByEmail(employee.getEmail()).isPresent()) {
			throw new ConflictException("An employee with email '" + employee.getEmail() + "' already exists.");
		}
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		return repository.save(employee);
	}

	public Employee update(Integer id, Employee employee) {
		Employee existing = this.findById(id);

		if (employee.getEmail() != null && !employee.getEmail().equalsIgnoreCase(existing.getEmail())) {
			repository.findByEmail(employee.getEmail()).ifPresent(other -> {
				if (!other.getId().equals(id)) {
					throw new ConflictException("An employee with email '" + employee.getEmail() + "' already exists.");
				}
			});
		}

		existing.setName(employee.getName());
		existing.setEmail(employee.getEmail());
		if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
			existing.setPassword(passwordEncoder.encode(employee.getPassword()));
		}
		existing.setDepartment(employee.getDepartment());
		if (employee.getRoles() != null) {
			existing.setRoles(employee.getRoles());
		}

		return repository.save(existing);
	}

	public void delete(Integer id) {
		Employee employee = this.findById(id);
		repository.delete(employee);
	}
}
