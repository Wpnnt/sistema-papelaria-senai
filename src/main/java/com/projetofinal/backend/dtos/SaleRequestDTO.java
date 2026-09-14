package com.projetofinal.backend.dtos;

import java.util.List;

import com.projetofinal.backend.enums.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SaleRequestDTO {

	@NotEmpty
	@Valid
	private List<SaleItemRequestDTO> items;

	@NotNull
	private PaymentMethod paymentMethod;

	private Float discountAmount;
	private Float amountPaid;
	private Integer employeeId;

	public SaleRequestDTO() {}

	public List<SaleItemRequestDTO> getItems() {
		return items;
	}

	public void setItems(List<SaleItemRequestDTO> items) {
		this.items = items;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Float discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Float getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Float amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
}
