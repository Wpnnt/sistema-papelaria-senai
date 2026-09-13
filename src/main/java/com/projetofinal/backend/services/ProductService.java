package com.projetofinal.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.exceptions.NotFoundException;
import com.projetofinal.backend.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	public List<Product> findAll() {
		return repository.findAll();
	}

	public Product findById(Integer id) {
		return repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
	}

	public Product create(Product product) {
		return repository.save(product);
	}

	public Product update(Integer id, Product product) {
		Product existing = this.findById(id);

		existing.setName(product.getName());
		existing.setCategory(product.getCategory());
		existing.setQuantity(product.getQuantity());
		existing.setPrice(product.getPrice());

		return repository.save(existing);
	}

	public void delete(Integer id) {
		Product product = this.findById(id);
		repository.delete(product);
	}
}
