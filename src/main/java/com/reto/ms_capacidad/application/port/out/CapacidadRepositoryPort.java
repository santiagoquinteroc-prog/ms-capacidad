package com.reto.ms_capacidad.application.port.out;

import com.reto.ms_capacidad.domain.Capacidad;
import reactor.core.publisher.Mono;

public interface CapacidadRepositoryPort {
	Mono<Capacidad> save(Capacidad capacidad);
}

