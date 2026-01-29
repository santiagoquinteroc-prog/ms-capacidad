package com.reto.ms_capacidad.adapters.in.web.router;

import com.reto.ms_capacidad.adapters.in.web.dto.request.CreateCapacidadRequest;
import com.reto.ms_capacidad.adapters.in.web.dto.response.CapacidadResponse;
import com.reto.ms_capacidad.adapters.in.web.handler.CapacidadHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class CapacidadRouter {
	@Bean
	@RouterOperations({
		@RouterOperation(
			path = "/capacidades",
			produces = {MediaType.APPLICATION_JSON_VALUE},
			method = RequestMethod.POST,
			beanClass = CapacidadHandler.class,
			beanMethod = "create",
			operation = @Operation(
				operationId = "createCapacidad",
				summary = "Crear capacidad",
				tags = {"Capacidades"},
				requestBody = @RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = CreateCapacidadRequest.class))
				),
				responses = {
					@ApiResponse(responseCode = "201", description = "Creado",
						content = @Content(schema = @Schema(implementation = CapacidadResponse.class))),
					@ApiResponse(responseCode = "400", description = "Solicitud inválida"),
					@ApiResponse(responseCode = "404", description = "Tecnología no encontrada"),
					@ApiResponse(responseCode = "409", description = "Nombre duplicado")
				}
			)
		)
	})
	public RouterFunction<ServerResponse> capacidadRoutes(CapacidadHandler handler) {
		return RouterFunctions.route()
			.POST("/capacidades", accept(MediaType.APPLICATION_JSON), handler::create)
			.build();
	}
}

