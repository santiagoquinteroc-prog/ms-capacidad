package com.reto.ms_capacidad.adapters.out.persistence.adapter;

import com.reto.ms_capacidad.adapters.out.persistence.entity.CapacidadEntity;
import com.reto.ms_capacidad.adapters.out.persistence.repository.CapacidadR2dbcRepository;
import com.reto.ms_capacidad.application.port.out.CapacidadRepositoryPort;
import com.reto.ms_capacidad.domain.Capacidad;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CapacidadRepositoryAdapter implements CapacidadRepositoryPort {
	private final CapacidadR2dbcRepository repository;

	public CapacidadRepositoryAdapter(CapacidadR2dbcRepository repository) {
		this.repository = repository;
	}

	@Override
	public Mono<Capacidad> save(Capacidad capacidad) {
		CapacidadEntity entity = toEntity(capacidad);
		return repository.save(entity)
			.map(this::toDomain);
	}

	private CapacidadEntity toEntity(Capacidad capacidad) {
		return new CapacidadEntity(
			capacidad.getId(),
			capacidad.getNombre(),
			capacidad.getDescripcion()
		);
	}

	private Capacidad toDomain(CapacidadEntity entity) {
		return new Capacidad(
			entity.getId(),
			entity.getNombre(),
			entity.getDescripcion()
		);
	}
}

