package com.projetofinal.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.projetofinal.backend.dtos.SaleItemRequestDTO;
import com.projetofinal.backend.dtos.SaleRequestDTO;
import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.entities.Sale;
import com.projetofinal.backend.enums.PaymentMethod;
import com.projetofinal.backend.exceptions.BadRequestException;
import com.projetofinal.backend.repositories.ProductRepository;
import com.projetofinal.backend.repositories.SaleRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SaleServiceTest {

	@Autowired
	private SaleService saleService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SaleRepository saleRepository;

	private Product notebook;
	private Product pen;

	@BeforeEach
	public void setup() {
		saleRepository.deleteAll();
		productRepository.deleteAll();

		notebook = new Product(null, "Caderno Espiral 96 Fls", "Papelaria", 20, 15.50f);
		pen = new Product(null, "Caneta Esferografica Azul", "Escrita", 50, 2.50f);

		notebook = productRepository.save(notebook);
		pen = productRepository.save(pen);
	}

	@Test
	public void shouldCreateSaleAndDeductStockSuccessfully() {
		SaleRequestDTO request = new SaleRequestDTO();
		request.setPaymentMethod(PaymentMethod.DINHEIRO);
		request.setAmountPaid(50.00f);
		request.setDiscountAmount(0.00f);

		SaleItemRequestDTO item1 = new SaleItemRequestDTO(notebook.getId(), 2);
		SaleItemRequestDTO item2 = new SaleItemRequestDTO(pen.getId(), 4);
		request.setItems(List.of(item1, item2));

		Sale sale = saleService.createSale(request);

		assertNotNull(sale.getId());
		assertEquals(41.00f, sale.getTotalAmount(), 0.01f);
		assertEquals(9.00f, sale.getChangeAmount(), 0.01f);
		assertEquals(2, sale.getItems().size());

		Product updatedNotebook = productRepository.findById(notebook.getId()).orElseThrow();
		Product updatedPen = productRepository.findById(pen.getId()).orElseThrow();

		assertEquals(18, updatedNotebook.getQuantity());
		assertEquals(46, updatedPen.getQuantity());
	}

	@Test
	public void shouldFailWhenStockIsInsufficient() {
		SaleRequestDTO request = new SaleRequestDTO();
		request.setPaymentMethod(PaymentMethod.PIX);
		request.setItems(List.of(new SaleItemRequestDTO(notebook.getId(), 25)));

		assertThrows(BadRequestException.class, () -> saleService.createSale(request));

		Product unchanged = productRepository.findById(notebook.getId()).orElseThrow();
		assertEquals(20, unchanged.getQuantity());
	}
}
