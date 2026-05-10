package com.banco.api.strategy;

import com.banco.api.model.TipoMovimiento;

public class ComisionStrategyFactory {
    public static ComisionStrategy getStrategy(TipoMovimiento tipo) {
        return switch (tipo) {
            case CREDITO -> new ComisionCreditoStrategy();
            case DEBITO -> new ComisionDebitoStrategy();
        };
    }
}
