package com.reto.ms_capacidad.adapters.out.persistence.repository;

import com.reto.ms_capacidad.adapters.out.persistence.entity.CapacidadTecnologiaEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CapacidadTecnologiaR2dbcRepository extends R2dbcRepository<CapacidadTecnologiaEntity, Long> {
	Flux<CapacidadTecnologiaEntity> findByCapacidadId(Long capacidadId);
}

