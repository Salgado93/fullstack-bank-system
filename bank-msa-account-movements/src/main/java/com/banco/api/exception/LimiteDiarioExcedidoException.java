package com.banco.api.exception;

public class LimiteDiarioExcedidoException extends RuntimeException {
    public LimiteDiarioExcedidoException(String mensaje) {
        super(mensaje);
    }
}
