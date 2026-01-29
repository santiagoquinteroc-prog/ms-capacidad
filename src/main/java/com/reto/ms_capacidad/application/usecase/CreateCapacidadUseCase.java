package com.reto.ms_capacidad.application.usecase;

import com.reto.ms_capacidad.application.port.in.CreateCapacidadPort;
import com.reto.ms_capacidad.application.port.out.CapacidadRepositoryPort;
import com.reto.ms_capacidad.domain.Capacidad;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateCapacidadUseCase implements CreateCapacidadPort {
	private final CapacidadRepositoryPort capacidadRepositoryPort;

	public CreateCapacidadUseCase(CapacidadRepositoryPort capacidadRepositoryPort) {
		this.capacidadRepositoryPort = capacidadRepositoryPort;
	}

	@Override
	public Mono<Capacidad> create(Capacidad capacidad) {
		return capacidadRepositoryPort.save(capacidad);
	}
}

