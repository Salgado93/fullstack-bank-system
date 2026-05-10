package com.banco.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteResponseDTO {
    private String cliente;
    private String fechaInicio;
    private String fechaFin;
    private List<ReporteMovimientoDTO> movimientos;
    private BigDecimal totalCreditos;
    private BigDecimal totalDebitos;
    private String reportePdfBase64;
}
