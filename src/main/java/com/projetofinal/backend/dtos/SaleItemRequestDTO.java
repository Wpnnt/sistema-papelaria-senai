package com.projetofinal.backend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SaleItemRequestDTO {

	@NotNull
	private Integer productId;

	@NotNull
	@Min(1)
	private Integer quantity;

	public SaleItemRequestDTO() {}

	public SaleItemRequestDTO(Integer productId, Integer quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
