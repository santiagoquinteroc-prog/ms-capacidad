package com.reto.ms_capacidad.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCapacidadRequest {
	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
	private String nombre;

	@Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
	private String descripcion;

	@NotNull(message = "Los IDs de tecnología son obligatorios")
	@Size(min = 3, max = 20, message = "Debe tener entre 3 y 20 tecnologías")
	private List<Long> tecnologiaIds;
}

