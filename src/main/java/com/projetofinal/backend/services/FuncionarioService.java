package com.projetofinal.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.Funcionario;
import com.projetofinal.backend.exceptions.NotFoundException;
import com.projetofinal.backend.repositories.FuncionarioRepository;

@Service
public class FuncionarioService {

	@Autowired
	private FuncionarioRepository repository;

	public List<Funcionario> procuraTodos() {
		return repository.findAll();
	}

	public Funcionario procuraPorId(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new NotFoundException("ID inválido! Funcionario não encontrado."));
	}

	public String adicionaFuncionario(Funcionario funcionario) {
		funcionario.setSenha(new BCryptPasswordEncoder().encode(funcionario.getSenha()));
		repository.save(funcionario);
		return "Funcionario adicionado com sucesso!";
	}

	public String editarFuncionario(Integer id, Funcionario funcionario) {
		Funcionario response = repository.findById(id).get();

		response.setNome(funcionario.getNome());
		response.setEmail(funcionario.getEmail());
		response.setSenha(funcionario.getSenha());

		repository.save(response);
		return "Usuário editado com sucesso!";
	}

	public void excluirFuncionario(Integer id) {
		Funcionario response = repository.findById(id).get();
		repository.delete(response);
	}
}
