package com.reto.ms_capacidad.adapters.in.web.dto.mapper;

import com.reto.ms_capacidad.adapters.in.web.dto.request.CreateCapacidadRequest;
import com.reto.ms_capacidad.adapters.in.web.dto.response.CapacidadResponse;
import com.reto.ms_capacidad.domain.Capacidad;
import org.springframework.stereotype.Component;

@Component
public class CapacidadMapper {
	public Capacidad toDomain(CreateCapacidadRequest request) {
		Capacidad capacidad = new Capacidad();
		capacidad.setNombre(request.getNombre());
		capacidad.setDescripcion(request.getDescripcion());
		return capacidad;
	}

	public CapacidadResponse toResponse(Capacidad capacidad) {
		return new CapacidadResponse(
			capacidad.getId(),
			capacidad.getNombre(),
			capacidad.getDescripcion()
		);
	}
}

