package com.reto.ms_capacidad.application.port.out;

import reactor.core.publisher.Mono;

public interface GetTecnologiaPort {
	Mono<TecnologiaInfo> getById(Long id);
	
	class TecnologiaInfo {
		private Long id;
		private String nombre;
		
		public TecnologiaInfo() {
		}
		
		public TecnologiaInfo(Long id, String nombre) {
			this.id = id;
			this.nombre = nombre;
		}
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
		
		public String getNombre() {
			return nombre;
		}
		
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
	}
}

