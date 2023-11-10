package com.projetofinal.backend.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.Funcionario;
import com.projetofinal.backend.exceptions.NotFoundException;
import com.projetofinal.backend.repositories.FuncionarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService implements UserDetailsService {

	@Autowired
	FuncionarioRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Funcionario usuario = repository.findByEmail(username)
				.orElseThrow(() -> new NotFoundException("Não encontrado"));
		return new User(usuario.getUsername(), usuario.getPassword(), true, true, true, true, usuario.getAuthorities());
	}

}
