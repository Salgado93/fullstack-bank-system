package com.banco.api.service;

import com.banco.api.dto.ReporteResponseDTO;

import java.time.LocalDate;

public interface ReporteService {
    ReporteResponseDTO generarReporte(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
