package com.reto.ms_capacidad.adapters.in.web.handler;

import com.reto.ms_capacidad.adapters.in.web.dto.mapper.CapacidadMapper;
import com.reto.ms_capacidad.adapters.in.web.dto.request.CreateCapacidadRequest;
import com.reto.ms_capacidad.adapters.in.web.dto.response.CapacidadListResponse;
import com.reto.ms_capacidad.domain.exception.NombreDuplicadoException;
import com.reto.ms_capacidad.domain.exception.TecnologiaNotFoundException;
import com.reto.ms_capacidad.domain.exception.TecnologiaServiceException;
import com.reto.ms_capacidad.application.port.in.CreateCapacidadPort;
import com.reto.ms_capacidad.application.port.in.GetCapacidadByIdPort;
import com.reto.ms_capacidad.application.port.in.ListCapacidadesPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class CapacidadHandler {
	private final CreateCapacidadPort createCapacidadPort;
	private final GetCapacidadByIdPort getCapacidadByIdPort;
	private final ListCapacidadesPort listCapacidadesPort;
	private final CapacidadMapper capacidadMapper;

	public CapacidadHandler(
		CreateCapacidadPort createCapacidadPort,
		GetCapacidadByIdPort getCapacidadByIdPort,
		ListCapacidadesPort listCapacidadesPort,
		CapacidadMapper capacidadMapper
	) {
		this.createCapacidadPort = createCapacidadPort;
		this.getCapacidadByIdPort = getCapacidadByIdPort;
		this.listCapacidadesPort = listCapacidadesPort;
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

	public Mono<ServerResponse> list(ServerRequest request) {
		int page = parseIntOrDefault(request.queryParam("page").orElse("0"), 0);
		int size = parseIntOrDefault(request.queryParam("size").orElse("10"), 10);
		String sortBy = request.queryParam("sortBy").orElse("nombre");
		String direction = request.queryParam("direction").orElse("asc");

		if (page < 0 || size <= 0) {
			return ServerResponse.status(HttpStatus.BAD_REQUEST)
				.bodyValue(new ErrorResponse("Parámetros de paginación inválidos"));
		}

		if (!sortBy.equals("nombre") && !sortBy.equals("cantidadTecnologias")) {
			return ServerResponse.status(HttpStatus.BAD_REQUEST)
				.bodyValue(new ErrorResponse("sortBy debe ser 'nombre' o 'cantidadTecnologias'"));
		}

		if (!direction.equals("asc") && !direction.equals("desc")) {
			return ServerResponse.status(HttpStatus.BAD_REQUEST)
				.bodyValue(new ErrorResponse("direction debe ser 'asc' o 'desc'"));
		}

		Mono<Tuple2<java.util.List<com.reto.ms_capacidad.adapters.in.web.dto.response.CapacidadResponse>, Long>> result = 
			listCapacidadesPort.findAll(page, size, sortBy, direction)
				.map(capacidadMapper::toResponse)
				.collectList()
				.zipWith(listCapacidadesPort.count());

		return result
			.map(tuple -> {
				java.util.List<com.reto.ms_capacidad.adapters.in.web.dto.response.CapacidadResponse> items = tuple.getT1();
				Long totalElements = tuple.getT2();
				return new CapacidadListResponse(page, size, totalElements, items);
			})
			.flatMap(response -> ServerResponse.ok().bodyValue(response))
			.onErrorResume(TecnologiaServiceException.class, ex -> 
				ServerResponse.status(HttpStatus.BAD_GATEWAY)
					.bodyValue(new ErrorResponse(ex.getMessage())))
			.onErrorResume(ex -> 
				ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.bodyValue(new ErrorResponse(ex.getMessage())));
	}

	public Mono<ServerResponse> getById(ServerRequest request) {
		try {
			Long id = Long.parseLong(request.pathVariable("id"));
			
			return getCapacidadByIdPort.getById(id)
				.flatMap(capacidadConTecnologias -> {
					var response = capacidadMapper.toResponse(capacidadConTecnologias);
					return ServerResponse.ok().bodyValue(response);
				})
				.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
					.bodyValue(new ErrorResponse("Capacidad con ID " + id + " no encontrada")))
				.onErrorResume(TecnologiaServiceException.class, ex -> 
					ServerResponse.status(HttpStatus.BAD_GATEWAY)
						.bodyValue(new ErrorResponse(ex.getMessage())));
		} catch (NumberFormatException ex) {
			return ServerResponse.status(HttpStatus.BAD_REQUEST)
				.bodyValue(new ErrorResponse("ID inválido"));
		}
	}

	private int parseIntOrDefault(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
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

