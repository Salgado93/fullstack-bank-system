package com.banco.api.dto;

import com.banco.api.model.TipoCuenta;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaRequestDTO {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo")
    private BigDecimal saldoInicial;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    private Boolean estado = true;
}
