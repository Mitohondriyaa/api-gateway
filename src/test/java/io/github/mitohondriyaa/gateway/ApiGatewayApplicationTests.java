package io.github.mitohondriyaa.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ApiGatewayApplicationTests {
	static MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
	final WebTestClient webTestClient;
	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;
	String token;

	@BeforeAll
	static void preparingFormData() {
		formData.add("grant_type", "password");
		formData.add("client_id", "test-client");
		formData.add("client_secret", "QyWux7rAfgr5h4ut1IzXwhuM7LOvrNDA");
		formData.add("username", "test_user");
		formData.add("password", "12345");
	}

	@BeforeEach
	void setUp() {
		token = webTestClient.post()
			.uri(issuerUri + "/protocol/openid-connect/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.bodyValue(formData)
			.exchange()
			.expectStatus().isOk()
			.expectBody(JsonNode.class)
			.returnResult()
			.getResponseBody()
			.path("access_token").asText();
	}

	@Test
	void shouldProxyToProductService() {
		webTestClient.get()
			.uri("/api/product")
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().isOk();
	}

	@Test
	void shouldProxyToOrderService() {
		webTestClient.get()
			.uri("/api/order/my")
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().isOk();
	}

	@Test
	void shouldProxyToInventoryService() {
		webTestClient.get()
			.uri("/api/inventory")
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().isOk();
	}
}
