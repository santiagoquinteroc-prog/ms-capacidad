package com.reto.ms_capacidad.application.usecase;

import com.reto.ms_capacidad.application.port.in.CreateCapacidadPort;
import com.reto.ms_capacidad.application.port.out.CapacidadRepositoryPort;
import com.reto.ms_capacidad.application.port.out.TecnologiaPort;
import com.reto.ms_capacidad.domain.Capacidad;
import com.reto.ms_capacidad.domain.exception.NombreDuplicadoException;
import com.reto.ms_capacidad.domain.exception.TecnologiaNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CreateCapacidadUseCase implements CreateCapacidadPort {
	private final CapacidadRepositoryPort capacidadRepositoryPort;
	private final TecnologiaPort tecnologiaPort;

	public CreateCapacidadUseCase(
		CapacidadRepositoryPort capacidadRepositoryPort,
		TecnologiaPort tecnologiaPort
	) {
		this.capacidadRepositoryPort = capacidadRepositoryPort;
		this.tecnologiaPort = tecnologiaPort;
	}

	@Override
	public Mono<Capacidad> create(Capacidad capacidad) {
		return validateTecnologias(capacidad.getTecnologiaIds())
			.then(validateNombreUnico(capacidad.getNombre()))
			.then(capacidadRepositoryPort.save(capacidad));
	}

	private Mono<Void> validateTecnologias(List<Long> tecnologiaIds) {
		if (tecnologiaIds == null || tecnologiaIds.isEmpty()) {
			return Mono.error(new IllegalArgumentException("Los IDs de tecnología son obligatorios"));
		}

		if (tecnologiaIds.size() < 3) {
			return Mono.error(new IllegalArgumentException("Debe tener mínimo 3 tecnologías"));
		}

		if (tecnologiaIds.size() > 20) {
			return Mono.error(new IllegalArgumentException("Debe tener máximo 20 tecnologías"));
		}

		Set<Long> uniqueIds = new HashSet<>(tecnologiaIds);
		if (uniqueIds.size() != tecnologiaIds.size()) {
			return Mono.error(new IllegalArgumentException("Los IDs de tecnología no pueden tener duplicados"));
		}

		return Flux.fromIterable(tecnologiaIds)
			.flatMap(tecnologiaId -> tecnologiaPort.exists(tecnologiaId)
				.flatMap(exists -> {
					if (!exists) {
						return Mono.error(new TecnologiaNotFoundException("La tecnología con ID " + tecnologiaId + " no existe"));
					}
					return Mono.just(true);
				}))
			.then();
	}

	private Mono<Void> validateNombreUnico(String nombre) {
		return capacidadRepositoryPort.existsByNombre(nombre)
			.flatMap(exists -> {
				if (exists) {
					return Mono.error(new NombreDuplicadoException("El nombre ya existe: " + nombre));
				}
				return Mono.empty();
			});
	}
}

