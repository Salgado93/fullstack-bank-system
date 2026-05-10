package com.banco.api.strategy;

import java.math.BigDecimal;

public class ComisionCreditoStrategy implements ComisionStrategy {
    @Override
    public BigDecimal calcularComision(BigDecimal monto) {
        // Sin comisión para créditos
        return BigDecimal.ZERO;
    }
}
