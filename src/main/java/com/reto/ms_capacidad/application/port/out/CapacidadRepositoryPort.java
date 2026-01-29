package com.reto.ms_capacidad.application.port.out;

import com.reto.ms_capacidad.domain.Capacidad;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadRepositoryPort {
	Mono<Capacidad> save(Capacidad capacidad);
	Mono<Boolean> existsByNombre(String nombre);
	Flux<Capacidad> findAll(int page, int size, String sortBy, String direction);
	Mono<Long> count();
	Flux<Long> findTecnologiaIdsByCapacidadId(Long capacidadId);
}

