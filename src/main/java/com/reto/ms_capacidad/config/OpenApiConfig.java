package com.reto.ms_capacidad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OpenApiConfig {
	@Bean
	public RouterFunction<ServerResponse> openApiRoutes() {
		return RouterFunctions.route()
			.GET("/swagger-ui.html", request -> ServerResponse.temporaryRedirect(java.net.URI.create("/swagger-ui/index.html")).build())
			.build();
	}
}

