package com.reto.ms_capacidad.adapters.in.web.handler;

import com.reto.ms_capacidad.adapters.in.web.dto.mapper.CapacidadMapper;
import com.reto.ms_capacidad.adapters.in.web.dto.request.CreateCapacidadRequest;
import com.reto.ms_capacidad.domain.exception.NombreDuplicadoException;
import com.reto.ms_capacidad.domain.exception.TecnologiaNotFoundException;
import com.reto.ms_capacidad.application.port.in.CreateCapacidadPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CapacidadHandler {
	private final CreateCapacidadPort createCapacidadPort;
	private final CapacidadMapper capacidadMapper;

	public CapacidadHandler(CreateCapacidadPort createCapacidadPort, CapacidadMapper capacidadMapper) {
		this.createCapacidadPort = createCapacidadPort;
		this.capacidadMapper = capacidadMapper;
	}

	public Mono<ServerResponse> create(ServerRequest request) {
		return request.bodyToMono(CreateCapacidadRequest.class)
			.flatMap(createRequest -> {
				var capacidad = capacidadMapper.toDomain(createRequest);
				return createCapacidadPort.create(capacidad)
					.map(capacidadMapper::toResponse)
					.flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
			})
			.onErrorResume(TecnologiaNotFoundException.class, ex -> 
				ServerResponse.status(HttpStatus.NOT_FOUND)
					.bodyValue(new ErrorResponse(ex.getMessage())))
			.onErrorResume(NombreDuplicadoException.class, ex -> 
				ServerResponse.status(HttpStatus.CONFLICT)
					.bodyValue(new ErrorResponse(ex.getMessage())))
			.onErrorResume(IllegalArgumentException.class, ex -> 
				ServerResponse.status(HttpStatus.BAD_REQUEST)
					.bodyValue(new ErrorResponse(ex.getMessage())))
			.onErrorResume(org.springframework.web.bind.support.WebExchangeBindException.class, ex -> 
				ServerResponse.status(HttpStatus.BAD_REQUEST)
					.bodyValue(new ErrorResponse("Error de validación: " + ex.getMessage())))
			.onErrorResume(ex -> 
				ServerResponse.status(HttpStatus.BAD_REQUEST)
					.bodyValue(new ErrorResponse(ex.getMessage())));
	}

	private static class ErrorResponse {
		private String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}

