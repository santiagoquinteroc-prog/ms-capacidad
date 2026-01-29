package com.reto.ms_capacidad.adapters.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadEntity {
	@Id
	private Long id;
	private String nombre;
	private String descripcion;
}

