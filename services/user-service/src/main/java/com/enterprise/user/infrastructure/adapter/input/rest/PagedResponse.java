package com.enterprise.user.infrastructure.adapter.input.rest;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Contenedor inmutable y genérico para respuestas paginadas de la API.
 * <p>
 * Evita la "fuga de abstracciones" de Spring Data hacia los clientes externos,
 * reduciendo el ruido en el JSON y controlando el contrato de la API.
 * </p>
 */
public record PagedResponse<T>(
    List<T> content,      // La lista de elementos (ej: usuarios)
    int page,             // Número de página actual
    int size,             // Tamaño de la página
    long totalElements,   // Total de registros en la base de datos
    int totalPages,       // Total de páginas calculadas
    boolean last          // ¿Es la última página?
) {
    /**
     * Factory Method de nivel Senior.
     * Convierte de forma limpia y genérica un Page de Spring en nuestro PagedResponse agnóstico.
     */
    public static <X> PagedResponse<X> from(Page<X> springPage) {
        return new PagedResponse<>(
            springPage.getContent(),
            springPage.getNumber(),
            springPage.getSize(),
            springPage.getTotalElements(),
            springPage.getTotalPages(),
            springPage.isLast()
        );
    }
}