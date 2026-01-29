package com.reto.ms_capacidad.adapters.out.webclient;

import com.reto.ms_capacidad.application.port.out.GetTecnologiaPort;
import com.reto.ms_capacidad.application.port.out.TecnologiaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class TecnologiaWebClientAdapter implements TecnologiaPort, GetTecnologiaPort {
	private final WebClient webClient;

	public TecnologiaWebClientAdapter(@Value("${ms.tecnologia.url:http://localhost:8080}") String baseUrl) {
		this.webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.build();
	}

	@Override
	public Mono<Boolean> exists(Long tecnologiaId) {
		return webClient.get()
			.uri("/tecnologias/{id}", tecnologiaId)
			.retrieve()
			.bodyToMono(String.class)
			.map(response -> true)
			.onErrorResume(WebClientResponseException.class, ex -> {
				if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
					return Mono.just(false);
				}
				return Mono.error(ex);
			})
			.onErrorReturn(false);
	}

	@Override
	public Mono<TecnologiaInfo> getById(Long id) {
		return webClient.get()
			.uri("/tecnologias/{id}", id)
			.retrieve()
			.bodyToMono(TecnologiaResponse.class)
			.map(response -> new TecnologiaInfo(response.getId(), response.getNombre()))
			.onErrorMap(WebClientResponseException.class, ex -> {
				if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
					return new RuntimeException("Tecnología con ID " + id + " no encontrada");
				}
				return new RuntimeException("Error al obtener tecnología con ID " + id + ": " + ex.getMessage(), ex);
			});
	}

	private static class TecnologiaResponse {
		private Long id;
		private String nombre;
		private String descripcion;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}
	}
}

