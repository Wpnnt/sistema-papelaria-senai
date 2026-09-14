package com.projetofinal.backend.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projetofinal.backend.dtos.EmailDTO;
import com.projetofinal.backend.dtos.SaleRequestDTO;
import com.projetofinal.backend.dtos.StandardErrorDTO;
import com.projetofinal.backend.entities.Employee;
import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.entities.Sale;

public class ApiClient {

	private String baseUrl;
	private String username;
	private String password;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public ApiClient() {
		this("http://localhost:8080", "admin@email.com", "admin123");
	}

	public ApiClient(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
		this.username = username;
		this.password = password;
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(5))
				.build();
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	public void setCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	private String getAuthHeader() {
		if (username == null || username.isBlank()) {
			return null;
		}
		String credentials = username + ":" + (password != null ? password : "");
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}

	private HttpRequest.Builder createRequestBuilder(String path) {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + path))
				.timeout(Duration.ofSeconds(10))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json");

		String auth = getAuthHeader();
		if (auth != null) {
			builder.header("Authorization", auth);
		}
		return builder;
	}

	private void validateResponse(HttpResponse<String> response) throws ApiException {
		int code = response.statusCode();
		if (code >= 200 && code < 300) {
			return;
		}
		StandardErrorDTO errorDto = null;
		String body = response.body();
		if (body != null && !body.isBlank()) {
			try {
				errorDto = objectMapper.readValue(body, StandardErrorDTO.class);
			} catch (Exception ignored) {}
		}
		throw new ApiException(code, errorDto);
	}

	public boolean testConnection() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + "/v3/api-docs"))
					.timeout(Duration.ofSeconds(3))
					.GET()
					.build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200 || response.statusCode() == 401;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Product> getProducts() throws Exception {
		HttpRequest request = createRequestBuilder("/products").GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), new TypeReference<List<Product>>() {});
	}

	public Product createProduct(Product product) throws Exception {
		String json = objectMapper.writeValueAsString(product);
		HttpRequest request = createRequestBuilder("/products")
				.POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), Product.class);
	}

	public Product updateProduct(Integer id, Product product) throws Exception {
		String json = objectMapper.writeValueAsString(product);
		HttpRequest request = createRequestBuilder("/products/" + id)
				.PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), Product.class);
	}

	public void deleteProduct(Integer id) throws Exception {
		HttpRequest request = createRequestBuilder("/products/" + id).DELETE().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
	}

	public List<Employee> getEmployees() throws Exception {
		HttpRequest request = createRequestBuilder("/employees").GET().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), new TypeReference<List<Employee>>() {});
	}

	public Employee createEmployee(Employee employee) throws Exception {
		String json = objectMapper.writeValueAsString(employee);
		HttpRequest request = createRequestBuilder("/employees")
				.POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), Employee.class);
	}

	public Employee updateEmployee(Integer id, Employee employee) throws Exception {
		String json = objectMapper.writeValueAsString(employee);
		HttpRequest request = createRequestBuilder("/employees/" + id)
				.PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), Employee.class);
	}

	public void deleteEmployee(Integer id) throws Exception {
		HttpRequest request = createRequestBuilder("/employees/" + id).DELETE().build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
	}

	public void sendEmail(String from, String to, String subject, String body) throws Exception {
		EmailDTO dto = new EmailDTO();
		dto.setEmailFrom(from);
		dto.setEmailTo(to);
		dto.setSubject(subject);
		dto.setBody(body);

		String json = objectMapper.writeValueAsString(dto);
		HttpRequest request = createRequestBuilder("/emails")
				.POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
	}

	public Sale createSale(SaleRequestDTO request) throws Exception {
		String json = objectMapper.writeValueAsString(request);
		HttpRequest httpRequest = createRequestBuilder("/sales")
				.POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
				.build();
		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), Sale.class);
	}

	public List<Sale> getSales() throws Exception {
		HttpRequest httpRequest = createRequestBuilder("/sales").GET().build();
		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		validateResponse(response);
		return objectMapper.readValue(response.body(), new TypeReference<List<Sale>>() {});
	}
}
