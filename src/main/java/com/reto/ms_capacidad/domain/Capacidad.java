package com.reto.ms_capacidad.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Capacidad {
	private Long id;
	private String nombre;
	private String descripcion;
	private List<Long> tecnologiaIds;
}

