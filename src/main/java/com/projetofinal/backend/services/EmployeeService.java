package com.projetofinal.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.Employee;
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
		if (employee.getPassword() != null) {
			employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		}
		return repository.save(employee);
	}

	public Employee update(Integer id, Employee employee) {
		Employee existing = this.findById(id);

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
