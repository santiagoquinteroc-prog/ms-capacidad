package com.reto.ms_capacidad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalErrorHandler {
	@Bean
	@Order(-2)
	public WebExceptionHandler webExceptionHandler() {
		return new GlobalErrorWebExceptionHandler();
	}

	private static class GlobalErrorWebExceptionHandler implements WebExceptionHandler {
		@Override
		public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
			DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
			
			exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
			
			String message = ex.getMessage() != null ? ex.getMessage() : "Internal server error";
			String json = "{\"message\":\"" + escapeJson(message) + "\"}";
			DataBuffer buffer = bufferFactory.wrap(json.getBytes());
			return exchange.getResponse().writeWith(Mono.just(buffer));
		}

		private String escapeJson(String str) {
			return str.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
		}
	}
}

