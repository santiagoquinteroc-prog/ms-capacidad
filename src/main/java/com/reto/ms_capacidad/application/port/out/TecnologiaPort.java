package com.reto.ms_capacidad.application.port.out;

import reactor.core.publisher.Mono;

public interface TecnologiaPort {
	Mono<Boolean> exists(Long tecnologiaId);
}

