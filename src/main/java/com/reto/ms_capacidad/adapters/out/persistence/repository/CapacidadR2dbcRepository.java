package com.reto.ms_capacidad.adapters.out.persistence.repository;

import com.reto.ms_capacidad.adapters.out.persistence.entity.CapacidadEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacidadR2dbcRepository extends ReactiveCrudRepository<CapacidadEntity, Long> {
}

