package com.reto.ms_capacidad.adapters.in.web.dto.mapper;

import com.reto.ms_capacidad.adapters.in.web.dto.request.CreateCapacidadRequest;
import com.reto.ms_capacidad.adapters.in.web.dto.response.CapacidadResponse;
import com.reto.ms_capacidad.adapters.in.web.dto.response.TecnologiaInfoResponse;
import com.reto.ms_capacidad.domain.Capacidad;
import com.reto.ms_capacidad.domain.CapacidadConTecnologias;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CapacidadMapper {
	public Capacidad toDomain(CreateCapacidadRequest request) {
		Capacidad capacidad = new Capacidad();
		capacidad.setNombre(request.getNombre());
		capacidad.setDescripcion(request.getDescripcion());
		capacidad.setTecnologiaIds(request.getTecnologiaIds());
		return capacidad;
	}

	public CapacidadResponse toResponse(Capacidad capacidad) {
		return new CapacidadResponse(
			capacidad.getId(),
			capacidad.getNombre(),
			capacidad.getDescripcion(),
			null,
			null
		);
	}

	public CapacidadResponse toResponse(CapacidadConTecnologias capacidadConTecnologias) {
		List<TecnologiaInfoResponse> tecnologias = capacidadConTecnologias.getTecnologias().stream()
			.map(t -> new TecnologiaInfoResponse(t.getId(), t.getNombre()))
			.collect(Collectors.toList());
		
		return new CapacidadResponse(
			capacidadConTecnologias.getId(),
			capacidadConTecnologias.getNombre(),
			capacidadConTecnologias.getDescripcion(),
			capacidadConTecnologias.getCantidadTecnologias(),
			tecnologias
		);
	}
}

