package com.reto.ms_capacidad.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Capacidad {
	private Long id;
	private String nombre;
	private String descripcion;
}

