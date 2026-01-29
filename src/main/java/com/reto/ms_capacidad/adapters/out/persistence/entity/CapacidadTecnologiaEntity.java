package com.reto.ms_capacidad.adapters.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacidad_tecnologia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadTecnologiaEntity {
	private Long capacidadId;
	private Long tecnologiaId;
}

