package com.reto.ms_capacidad.application.port.in;

import com.reto.ms_capacidad.domain.CapacidadConTecnologias;
import reactor.core.publisher.Mono;

public interface GetCapacidadByIdPort {
	Mono<CapacidadConTecnologias> getById(Long id);
}

