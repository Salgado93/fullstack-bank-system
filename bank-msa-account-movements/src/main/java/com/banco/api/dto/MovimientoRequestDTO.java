package com.banco.api.dto;

import com.banco.api.model.TipoMovimiento;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequestDTO {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a cero")
    private BigDecimal valor;

    @NotNull(message = "El ID de la cuenta es obligatorio")
    private Long cuentaId;
}
