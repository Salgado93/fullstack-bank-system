package com.banco.api.dto;

import com.banco.api.model.TipoMovimiento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponseDTO {
    private Long movimientoId;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldoDisponible;
    private Boolean estado;
    private Long cuentaId;
    private String numeroCuenta;
}
