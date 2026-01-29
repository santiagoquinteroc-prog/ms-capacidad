package com.reto.ms_capacidad.application.port.in;

import com.reto.ms_capacidad.domain.CapacidadConTecnologias;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ListCapacidadesPort {
    Flux<CapacidadConTecnologias> findAll(int page, int size, String sortBy, String direction);

    Mono<Long> count();
}

