package com.reto.ms_capacidad.adapters.in.web.handler;

import com.reto.ms_capacidad.adapters.in.web.dto.mapper.CapacidadMapper;
import com.reto.ms_capacidad.adapters.in.web.dto.request.CreateCapacidadRequest;
import com.reto.ms_capacidad.application.port.in.CreateCapacidadPort;
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
					.flatMap(response -> ServerResponse.ok().bodyValue(response));
			});
	}
}

