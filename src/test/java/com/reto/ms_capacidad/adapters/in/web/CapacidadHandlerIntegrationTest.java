package com.reto.ms_capacidad.adapters.in.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CapacidadHandlerIntegrationTest {

	@Autowired
	private WebTestClient webTestClient;

	private WireMockServer wireMockServer;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("ms.tecnologia.url", () -> "http://localhost:8089");
	}

	@BeforeEach
	void setUp() {
		wireMockServer = new WireMockServer(wireMockConfig().port(8089));
		wireMockServer.start();
		WireMock.configureFor("localhost", 8089);
	}

	@AfterEach
	void tearDown() {
		wireMockServer.stop();
	}

	@Test
	void shouldListCapacidadesOrderedByNombreAsc() {
		stubFor(get(urlEqualTo("/tecnologias/1"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{\"id\":1,\"nombre\":\"Java\",\"descripcion\":\"Java\"}")));

		stubFor(get(urlEqualTo("/tecnologias/2"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{\"id\":2,\"nombre\":\"Spring\",\"descripcion\":\"Spring\"}")));

		webTestClient.get()
			.uri("/capacidades?page=0&size=10&sortBy=nombre&direction=asc")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.page").exists()
			.jsonPath("$.size").exists()
			.jsonPath("$.totalElements").exists()
			.jsonPath("$.items").isArray();
	}

	@Test
	void shouldListCapacidadesOrderedByCantidadTecnologiasDesc() {
		stubFor(get(urlEqualTo("/tecnologias/1"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{\"id\":1,\"nombre\":\"Java\",\"descripcion\":\"Java\"}")));

		stubFor(get(urlEqualTo("/tecnologias/2"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{\"id\":2,\"nombre\":\"Spring\",\"descripcion\":\"Spring\"}")));

		webTestClient.get()
			.uri("/capacidades?page=0&size=10&sortBy=cantidadTecnologias&direction=desc")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.page").exists()
			.jsonPath("$.size").exists()
			.jsonPath("$.totalElements").exists()
			.jsonPath("$.items").isArray();
	}

	@Test
	void shouldHandlePagination() {
		stubFor(get(urlEqualTo("/tecnologias/1"))
			.willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("{\"id\":1,\"nombre\":\"Java\",\"descripcion\":\"Java\"}")));

		webTestClient.get()
			.uri("/capacidades?page=0&size=5&sortBy=nombre&direction=asc")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.page").isEqualTo(0)
			.jsonPath("$.size").isEqualTo(5);
	}

	@Test
	void shouldReturn502WhenTecnologiaServiceFails() {
		stubFor(get(urlMatching("/tecnologias/.*"))
			.willReturn(aResponse()
				.withStatus(500)
				.withBody("Internal Server Error")));

		webTestClient.get()
			.uri("/capacidades?page=0&size=10&sortBy=nombre&direction=asc")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadGateway();
	}
}

