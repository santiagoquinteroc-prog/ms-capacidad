package com.reto.ms_capacidad.application.usecase;

import com.reto.ms_capacidad.application.port.in.GetCapacidadByIdPort;
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
public class GetCapacidadByIdUseCase implements GetCapacidadByIdPort {
	private final CapacidadRepositoryPort capacidadRepositoryPort;
	private final GetTecnologiaPort getTecnologiaPort;

	public GetCapacidadByIdUseCase(
		CapacidadRepositoryPort capacidadRepositoryPort,
		GetTecnologiaPort getTecnologiaPort
	) {
		this.capacidadRepositoryPort = capacidadRepositoryPort;
		this.getTecnologiaPort = getTecnologiaPort;
	}

	@Override
	public Mono<CapacidadConTecnologias> getById(Long id) {
		return capacidadRepositoryPort.findById(id)
			.flatMap(this::enrichWithTecnologias);
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
					.map(tecnologias -> createCapacidadConTecnologias(capacidad, tecnologias));
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

