package com.aurealab.dto;

import lombok.*;

/**
 * Clase que representa una respuesta genérica para la API.
 *
 * @param <T> Tipo de datos que se incluye en la respuesta.
 */

@Builder
public record MenuDTO(
	Long id,
	String name,
	String father,
	String nameFather,
	String route,
	int orderMenu,
	String icon
) {}