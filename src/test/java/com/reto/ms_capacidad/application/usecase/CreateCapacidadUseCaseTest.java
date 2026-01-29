package com.reto.ms_capacidad.application.usecase;

import com.reto.ms_capacidad.application.port.out.CapacidadRepositoryPort;
import com.reto.ms_capacidad.application.port.out.TecnologiaPort;
import com.reto.ms_capacidad.domain.Capacidad;
import com.reto.ms_capacidad.domain.exception.NombreDuplicadoException;
import com.reto.ms_capacidad.domain.exception.TecnologiaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCapacidadUseCaseTest {

	@Mock
	private CapacidadRepositoryPort capacidadRepositoryPort;

	@Mock
	private TecnologiaPort tecnologiaPort;

	private CreateCapacidadUseCase createCapacidadUseCase;

	private Capacidad capacidad;

	@BeforeEach
	void setUp() {
		createCapacidadUseCase = new CreateCapacidadUseCase(capacidadRepositoryPort, tecnologiaPort);
		capacidad = new Capacidad();
		capacidad.setNombre("Backend");
		capacidad.setDescripcion("Capacidad backend");
	}

	@Test
	void shouldFailWhenTecnologiaIdsHasLessThan3Elements() {
		capacidad.setTecnologiaIds(Arrays.asList(1L, 2L));

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof IllegalArgumentException &&
				throwable.getMessage().equals("Debe tener mínimo 3 tecnologías"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
		verify(capacidadRepositoryPort, never()).existsByNombre(anyString());
		verify(tecnologiaPort, never()).exists(anyLong());
	}

	@Test
	void shouldFailWhenTecnologiaIdsHasMoreThan20Elements() {
		List<Long> tecnologiaIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L,
			11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L);
		capacidad.setTecnologiaIds(tecnologiaIds);

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof IllegalArgumentException &&
				throwable.getMessage().equals("Debe tener máximo 20 tecnologías"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
		verify(capacidadRepositoryPort, never()).existsByNombre(anyString());
		verify(tecnologiaPort, never()).exists(anyLong());
	}

	@Test
	void shouldFailWhenTecnologiaIdsHasDuplicates() {
		capacidad.setTecnologiaIds(Arrays.asList(1L, 2L, 3L, 1L));

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof IllegalArgumentException &&
				throwable.getMessage().equals("Los IDs de tecnología no pueden tener duplicados"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
		verify(capacidadRepositoryPort, never()).existsByNombre(anyString());
		verify(tecnologiaPort, never()).exists(anyLong());
	}

	@Test
	void shouldFailWhenTecnologiaDoesNotExist() {
		capacidad.setTecnologiaIds(Arrays.asList(1L, 2L, 3L));
		when(tecnologiaPort.exists(1L)).thenReturn(Mono.just(true));
		when(tecnologiaPort.exists(2L)).thenReturn(Mono.just(true));
		when(tecnologiaPort.exists(3L)).thenReturn(Mono.just(false));

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof TecnologiaNotFoundException &&
				throwable.getMessage().equals("La tecnología con ID 3 no existe"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
		verify(capacidadRepositoryPort, never()).existsByNombre(anyString());
	}

	@Test
	void shouldFailWhenNombreAlreadyExists() {
		capacidad.setTecnologiaIds(Arrays.asList(1L, 2L, 3L));
		when(tecnologiaPort.exists(anyLong())).thenReturn(Mono.just(true));
		when(capacidadRepositoryPort.existsByNombre("Backend")).thenReturn(Mono.just(true));

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof NombreDuplicadoException &&
				throwable.getMessage().equals("El nombre ya existe: Backend"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
	}

	@Test
	void shouldSucceedWhenAllValidationsPass() {
		capacidad.setTecnologiaIds(Arrays.asList(1L, 2L, 3L));
		when(tecnologiaPort.exists(anyLong())).thenReturn(Mono.just(true));
		when(capacidadRepositoryPort.existsByNombre("Backend")).thenReturn(Mono.just(false));
		
		Capacidad savedCapacidad = new Capacidad();
		savedCapacidad.setId(1L);
		savedCapacidad.setNombre("Backend");
		savedCapacidad.setDescripcion("Capacidad backend");
		savedCapacidad.setTecnologiaIds(Arrays.asList(1L, 2L, 3L));
		when(capacidadRepositoryPort.save(any(Capacidad.class))).thenReturn(Mono.just(savedCapacidad));

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectNextMatches(result -> 
				result.getId() != null &&
				result.getNombre().equals("Backend"))
			.verifyComplete();

		verify(tecnologiaPort, times(3)).exists(anyLong());
		verify(capacidadRepositoryPort).existsByNombre("Backend");
		verify(capacidadRepositoryPort).save(any(Capacidad.class));
	}

	@Test
	void shouldFailWhenTecnologiaIdsIsNull() {
		capacidad.setTecnologiaIds(null);

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof IllegalArgumentException &&
				throwable.getMessage().equals("Los IDs de tecnología son obligatorios"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
		verify(capacidadRepositoryPort, never()).existsByNombre(anyString());
		verify(tecnologiaPort, never()).exists(anyLong());
	}

	@Test
	void shouldFailWhenTecnologiaIdsIsEmpty() {
		capacidad.setTecnologiaIds(Collections.emptyList());

		StepVerifier.create(createCapacidadUseCase.create(capacidad))
			.expectErrorMatches(throwable -> 
				throwable instanceof IllegalArgumentException &&
				throwable.getMessage().equals("Los IDs de tecnología son obligatorios"))
			.verify();

		verify(capacidadRepositoryPort, never()).save(any(Capacidad.class));
		verify(capacidadRepositoryPort, never()).existsByNombre(anyString());
		verify(tecnologiaPort, never()).exists(anyLong());
	}
}

