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

	@Override
	public Flux<Capacidad> findAll(int page, int size, String sortBy, String direction) {
		String orderBy = buildOrderBy(sortBy, direction);
		int offset = page * size;
		
		String sql = "SELECT id, nombre, descripcion FROM capacidad " + orderBy + " LIMIT :size OFFSET :offset";
		
		return databaseClient.sql(sql)
			.bind("size", size)
			.bind("offset", offset)
			.fetch()
			.all()
			.map(this::rowToCapacidad);
	}

	@Override
	public Mono<Long> count() {
		return databaseClient.sql("SELECT COUNT(*) as count FROM capacidad")
			.fetch()
			.one()
			.map(row -> {
				Object countObj = row.get("count");
				if (countObj instanceof Number) {
					return ((Number) countObj).longValue();
				}
				return 0L;
			});
	}

	@Override
	public Flux<Long> findTecnologiaIdsByCapacidadId(Long capacidadId) {
		return databaseClient.sql("SELECT tecnologia_id FROM capacidad_tecnologia WHERE capacidad_id = :capacidadId")
			.bind("capacidadId", capacidadId)
			.fetch()
			.all()
			.map(row -> {
				Object idObj = row.get("tecnologia_id");
				if (idObj instanceof Number) {
					return ((Number) idObj).longValue();
				}
				return null;
			})
			.filter(id -> id != null);
	}

	private String buildOrderBy(String sortBy, String direction) {
		String orderColumn;
		if ("cantidadTecnologias".equals(sortBy)) {
			orderColumn = "(SELECT COUNT(*) FROM capacidad_tecnologia WHERE capacidad_tecnologia.capacidad_id = capacidad.id)";
		} else {
			orderColumn = "nombre";
		}
		String dir = "desc".equalsIgnoreCase(direction) ? "DESC" : "ASC";
		return "ORDER BY " + orderColumn + " " + dir;
	}

	private Capacidad rowToCapacidad(java.util.Map<String, Object> row) {
		Long id = getLongValue(row.get("id"));
		String nombre = (String) row.get("nombre");
		String descripcion = (String) row.get("descripcion");
		return new Capacidad(id, nombre, descripcion, null);
	}

	private Long getLongValue(Object value) {
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		return null;
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

