package com.projetofinal.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetofinal.backend.dtos.SaleItemRequestDTO;
import com.projetofinal.backend.dtos.SaleRequestDTO;
import com.projetofinal.backend.entities.Employee;
import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.entities.Sale;
import com.projetofinal.backend.entities.SaleItem;
import com.projetofinal.backend.exceptions.BadRequestException;
import com.projetofinal.backend.exceptions.NotFoundException;
import com.projetofinal.backend.repositories.EmployeeRepository;
import com.projetofinal.backend.repositories.ProductRepository;
import com.projetofinal.backend.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Transactional
	public Sale createSale(SaleRequestDTO request) {
		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new BadRequestException("Sale must contain at least one item.");
		}

		Sale sale = new Sale();

		if (request.getEmployeeId() != null) {
			Employee employee = employeeRepository.findById(request.getEmployeeId())
					.orElseThrow(() -> new NotFoundException("Employee not found with id: " + request.getEmployeeId()));
			sale.setEmployee(employee);
		}

		float grossTotal = 0.0f;

		for (SaleItemRequestDTO itemReq : request.getItems()) {
			Product product = productRepository.findById(itemReq.getProductId())
					.orElseThrow(() -> new NotFoundException("Product not found with id: " + itemReq.getProductId()));

			int availableStock = product.getQuantity() != null ? product.getQuantity() : 0;
			if (availableStock < itemReq.getQuantity()) {
				throw new BadRequestException("Insufficient stock for product '" + product.getName()
						+ "'. Available: " + availableStock + ", Requested: " + itemReq.getQuantity());
			}

			product.setQuantity(availableStock - itemReq.getQuantity());
			productRepository.save(product);

			float unitPrice = product.getPrice() != null ? product.getPrice() : 0.0f;
			float subtotal = unitPrice * itemReq.getQuantity();
			grossTotal += subtotal;

			SaleItem saleItem = new SaleItem(sale, product, itemReq.getQuantity(), unitPrice, subtotal);
			sale.addItem(saleItem);
		}

		float discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0f;
		float finalTotal = Math.max(0.0f, grossTotal - discount);

		sale.setTotalAmount(finalTotal);
		sale.setDiscountAmount(discount);
		sale.setPaymentMethod(request.getPaymentMethod());

		float paid = request.getAmountPaid() != null ? request.getAmountPaid() : finalTotal;
		sale.setAmountPaid(paid);

		float change = Math.max(0.0f, paid - finalTotal);
		sale.setChangeAmount(change);
		sale.setCreatedAt(LocalDateTime.now());

		return saleRepository.save(sale);
	}

	public List<Sale> findAll() {
		return saleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
	}

	public Sale findById(Long id) {
		return saleRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Sale record not found with id: " + id));
	}
}
