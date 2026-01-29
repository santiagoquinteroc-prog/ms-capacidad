package com.reto.ms_capacidad.adapters.out.webclient;

import com.reto.ms_capacidad.application.port.out.TecnologiaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class TecnologiaWebClientAdapter implements TecnologiaPort {
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
}

