package com.banco.api.dto;

import com.banco.api.model.TipoCuenta;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponseDTO {
    private Long cuentaId;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldo;
    private Boolean estado;
    private String clienteNombre;
    private Long clienteId;
}
