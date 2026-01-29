package com.reto.ms_capacidad.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadConTecnologias {
	private Long id;
	private String nombre;
	private String descripcion;
	private Integer cantidadTecnologias;
	private List<TecnologiaInfo> tecnologias;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TecnologiaInfo {
		private Long id;
		private String nombre;
	}
}


