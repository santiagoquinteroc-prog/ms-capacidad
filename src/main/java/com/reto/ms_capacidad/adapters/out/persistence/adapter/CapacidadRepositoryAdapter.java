package com.reto.ms_capacidad.adapters.out.persistence.adapter;

import com.reto.ms_capacidad.adapters.out.persistence.entity.CapacidadEntity;
import com.reto.ms_capacidad.adapters.out.persistence.repository.CapacidadR2dbcRepository;
import com.reto.ms_capacidad.application.port.out.CapacidadRepositoryPort;
import com.reto.ms_capacidad.domain.Capacidad;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CapacidadRepositoryAdapter implements CapacidadRepositoryPort {
	private final CapacidadR2dbcRepository repository;
	private final DatabaseClient databaseClient;

	public CapacidadRepositoryAdapter(
		CapacidadR2dbcRepository repository,
		DatabaseClient databaseClient
	) {
		this.repository = repository;
		this.databaseClient = databaseClient;
	}

	@Override
	public Mono<Capacidad> save(Capacidad capacidad) {
		CapacidadEntity entity = toEntity(capacidad);
		return repository.save(entity)
			.flatMap(savedEntity -> {
				if (capacidad.getTecnologiaIds() != null && !capacidad.getTecnologiaIds().isEmpty()) {
					return saveTecnologias(savedEntity.getId(), capacidad.getTecnologiaIds())
						.then(Mono.just(savedEntity));
				}
				return Mono.just(savedEntity);
			})
			.map(this::toDomain);
	}

	private Mono<Void> saveTecnologias(Long capacidadId, java.util.List<Long> tecnologiaIds) {
		return Flux.fromIterable(tecnologiaIds)
			.flatMap(tecnologiaId -> databaseClient.sql(
				"INSERT INTO capacidad_tecnologia (capacidad_id, tecnologia_id) VALUES (:capacidadId, :tecnologiaId)")
				.bind("capacidadId", capacidadId)
				.bind("tecnologiaId", tecnologiaId)
				.fetch()
				.rowsUpdated())
			.then();
	}

	@Override
	public Mono<Boolean> existsByNombre(String nombre) {
		return databaseClient.sql("SELECT COUNT(*) as count FROM capacidad WHERE nombre = :nombre")
			.bind("nombre", nombre)
			.fetch()
			.one()
			.map(row -> {
				Object countObj = row.get("count");
				if (countObj instanceof Number) {
					return ((Number) countObj).longValue() > 0;
				}
				return false;
			})
			.onErrorReturn(false);
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
			entity.getDescripcion(),
			null
		);
	}
}

