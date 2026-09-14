package com.projetofinal.backend.handler;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofinal.backend.entities.Employee;
import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.repositories.EmployeeRepository;
import com.projetofinal.backend.repositories.ProductRepository;
import com.projetofinal.backend.repositories.SaleRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private SaleRepository saleRepository;

	@BeforeEach
	public void setup() {
		saleRepository.deleteAll();
		productRepository.deleteAll();
		employeeRepository.deleteAll();
	}

	@Test
	@WithMockUser(authorities = {"ADMIN", "USER"})
	public void shouldReturn404StandardErrorWhenProductNotFound() throws Exception {
		mockMvc.perform(get("/products/999999")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource Not Found"))
				.andExpect(jsonPath("$.message", containsString("Product not found")))
				.andExpect(jsonPath("$.timestamp", notNullValue()))
				.andExpect(jsonPath("$.path").value("/products/999999"));
	}

	@Test
	@WithMockUser(authorities = {"ADMIN"})
	public void shouldReturn400ValidationErrorsWhenCreatingInvalidProduct() throws Exception {
		Product invalidProduct = new Product();
		invalidProduct.setName("");
		invalidProduct.setCategory("");
		invalidProduct.setPrice(-5.0f);
		invalidProduct.setQuantity(-10);

		mockMvc.perform(post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidProduct)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors", notNullValue()));
	}

	@Test
	@WithMockUser(authorities = {"ADMIN"})
	public void shouldReturn409ConflictWhenEmployeeEmailIsDuplicated() throws Exception {
		Employee existing = new Employee();
		existing.setName("Existing Staff");
		existing.setEmail("staff@senai.com");
		existing.setPassword("secret123");
		existing.setDepartment("TI");
		mockMvc.perform(post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(existing)))
				.andExpect(status().isCreated());

		Employee duplicate = new Employee();
		duplicate.setName("Duplicate Staff");
		duplicate.setEmail("staff@senai.com");
		duplicate.setPassword("otherpwd");
		duplicate.setDepartment("RH");

		mockMvc.perform(post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicate)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.error").value("Conflict"))
				.andExpect(jsonPath("$.message", containsString("already exists")));
	}

	@Test
	@WithMockUser(authorities = {"ADMIN", "USER"})
	public void shouldReturn400WhenSalePayloadHasMalformedOrEmptyItems() throws Exception {
		String invalidPayload = "{\"paymentMethod\": \"DINHEIRO\", \"items\": []}";

		mockMvc.perform(post("/sales")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidPayload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Validation Error"));
	}
}
