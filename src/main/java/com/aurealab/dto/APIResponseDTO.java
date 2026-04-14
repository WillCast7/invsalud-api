package com.aurealab.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase que representa una respuesta genérica para la API.
 *
 * @param <T> Tipo de datos que se incluye en la respuesta.
 */
@Data
@Builder
public class APIResponseDTO<T> implements Serializable {

	private final boolean state;
	private final String message;
	private final String error;
	private final String timestamp; // Eliminar inicialización directa
	private final Optional<T> data; // Usar Optional para evitar nulos
	private final Object pageable; // Puede ser Pageable o PageableResponseDTO

	// Constructor privado para forzar el uso del builder
	private APIResponseDTO(boolean state, String message, String error, String timestamp, Optional<T> data, Object pageable) {
		this.state = state;
		this.message = message;
		this.error = error;
		this.timestamp = timestamp; // Ahora se establece desde el builder
		this.data = data;
		this.pageable = pageable;
	}

	// Métodos estáticos para crear respuestas comunes
	public static <T> APIResponseDTO<T> success(T data, String message) {
		return APIResponseDTO.<T>builder()
				.state(true)
				.message(message)
				.timestamp(LocalDateTime.now().toString())
				.data(Optional.ofNullable(data))
				.build();
	}

	public static <T> APIResponseDTO<T> failure(String message, String error) {
		return APIResponseDTO.<T>builder()
				.state(false)
				.message(message)
				.error(error)
				.timestamp(LocalDateTime.now().toString())
				.data(Optional.empty())
				.build();
	}

	public static <T> APIResponseDTO<T> withPageable(T data, String message, Object pageable) {
		return APIResponseDTO.<T>builder()
				.state(true)
				.message(message)
				.timestamp(LocalDateTime.now().toString()) // Se establece aquí
				.data(Optional.ofNullable(data))
				.pageable(pageable) // Puede ser Pageable o PageableResponseDTO
				.build();
	}
}
