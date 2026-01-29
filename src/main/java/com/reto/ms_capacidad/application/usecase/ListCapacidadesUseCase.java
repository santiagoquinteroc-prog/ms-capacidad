package com.reto.ms_capacidad.application.usecase;

import com.reto.ms_capacidad.application.port.in.ListCapacidadesPort;
import com.reto.ms_capacidad.application.port.out.CapacidadRepositoryPort;
import com.reto.ms_capacidad.application.port.out.GetTecnologiaPort;
import com.reto.ms_capacidad.domain.Capacidad;
import com.reto.ms_capacidad.domain.CapacidadConTecnologias;
import com.reto.ms_capacidad.domain.exception.TecnologiaServiceException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ListCapacidadesUseCase implements ListCapacidadesPort {
	private final CapacidadRepositoryPort capacidadRepositoryPort;
	private final GetTecnologiaPort getTecnologiaPort;

	public ListCapacidadesUseCase(
		CapacidadRepositoryPort capacidadRepositoryPort,
		GetTecnologiaPort getTecnologiaPort
	) {
		this.capacidadRepositoryPort = capacidadRepositoryPort;
		this.getTecnologiaPort = getTecnologiaPort;
	}

	@Override
	public Flux<CapacidadConTecnologias> findAll(int page, int size, String sortBy, String direction) {
		return capacidadRepositoryPort.findAll(page, size, sortBy, direction)
			.flatMap(this::enrichWithTecnologias);
	}

	@Override
	public Mono<Long> count() {
		return capacidadRepositoryPort.count();
	}

	private Mono<CapacidadConTecnologias> enrichWithTecnologias(Capacidad capacidad) {
		return capacidadRepositoryPort.findTecnologiaIdsByCapacidadId(capacidad.getId())
			.collectList()
			.flatMap(tecnologiaIds -> {
				if (tecnologiaIds.isEmpty()) {
					return Mono.just(createCapacidadConTecnologias(capacidad, List.of()));
				}
				
				return Flux.fromIterable(tecnologiaIds)
					.flatMap(tecnologiaId -> getTecnologiaPort.getById(tecnologiaId)
						.onErrorMap(ex -> new TecnologiaServiceException("Error al obtener tecnología con ID " + tecnologiaId + ": " + ex.getMessage(), ex))
						, 10)
					.map(info -> new CapacidadConTecnologias.TecnologiaInfo(info.getId(), info.getNombre()))
					.collectList()
					.flatMap(tecnologias -> {
						if (tecnologias.size() != tecnologiaIds.size()) {
							return Mono.error(new TecnologiaServiceException("No se pudieron obtener todas las tecnologías"));
						}
						return Mono.just(createCapacidadConTecnologias(capacidad, tecnologias));
					});
			});
	}

	private CapacidadConTecnologias createCapacidadConTecnologias(Capacidad capacidad, List<CapacidadConTecnologias.TecnologiaInfo> tecnologias) {
		return new CapacidadConTecnologias(
			capacidad.getId(),
			capacidad.getNombre(),
			capacidad.getDescripcion(),
			tecnologias.size(),
			tecnologias
		);
	}
}

