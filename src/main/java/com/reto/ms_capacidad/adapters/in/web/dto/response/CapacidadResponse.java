package com.reto.ms_capacidad.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadResponse {
	private Long id;
	private String nombre;
	private String descripcion;
}

