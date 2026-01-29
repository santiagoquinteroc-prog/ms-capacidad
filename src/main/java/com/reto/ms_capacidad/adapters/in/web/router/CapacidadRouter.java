package com.reto.ms_capacidad.adapters.in.web.router;

import com.reto.ms_capacidad.adapters.in.web.handler.CapacidadHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CapacidadRouter {
	@Bean
	public RouterFunction<ServerResponse> capacidadRoutes(CapacidadHandler handler) {
		return RouterFunctions.route()
			.POST("/capacidades", handler::create)
			.build();
	}
}

