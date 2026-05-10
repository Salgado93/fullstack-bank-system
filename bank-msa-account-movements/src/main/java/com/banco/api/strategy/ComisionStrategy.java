package com.banco.api.strategy;

import java.math.BigDecimal;

public interface ComisionStrategy {
    BigDecimal calcularComision(BigDecimal monto);
}
