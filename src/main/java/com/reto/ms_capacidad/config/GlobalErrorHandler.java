package com.reto.ms_capacidad.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GlobalErrorHandler {
	@Bean
	public RouterFunction<ServerResponse> errorRoutes() {
		return RouterFunctions.route()
			.onError(Exception.class, (exception, request) -> {
				return ServerResponse.badRequest()
					.bodyValue(new ErrorResponse(exception.getMessage()));
			})
			.build();
	}

	@Data
	@AllArgsConstructor
	private static class ErrorResponse {
		private String message;
	}
}

