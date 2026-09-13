package com.projetofinal.backend.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.Employee;
import com.projetofinal.backend.exceptions.NotFoundException;
import com.projetofinal.backend.repositories.EmployeeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService implements UserDetailsService {

	@Autowired
	private EmployeeRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = repository.findByEmail(username)
				.orElseThrow(() -> new NotFoundException("Employee not found with email: " + username));
		return new User(employee.getUsername(), employee.getPassword(), true, true, true, true, employee.getAuthorities());
	}
}
