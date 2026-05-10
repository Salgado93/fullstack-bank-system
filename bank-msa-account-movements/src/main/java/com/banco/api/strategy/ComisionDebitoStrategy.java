package com.banco.api.strategy;

import java.math.BigDecimal;

public class ComisionDebitoStrategy implements ComisionStrategy {
    @Override
    public BigDecimal calcularComision(BigDecimal monto) {
        // Comisión fija de 0.50 para débitos (ejemplo)
        return new BigDecimal("0.50");
    }
}
