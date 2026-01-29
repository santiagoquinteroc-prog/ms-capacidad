package com.reto.ms_capacidad.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacidadListResponse {
	private Integer page;
	private Integer size;
	private Long totalElements;
	private List<CapacidadResponse> items;
}

