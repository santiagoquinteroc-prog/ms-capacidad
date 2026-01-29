package com.reto.ms_capacidad.application.port.in;

import com.reto.ms_capacidad.domain.Capacidad;
import reactor.core.publisher.Mono;

public interface CreateCapacidadPort {
	Mono<Capacidad> create(Capacidad capacidad);
}


