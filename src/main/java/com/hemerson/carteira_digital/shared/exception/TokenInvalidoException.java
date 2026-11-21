package com.hemerson.carteira_digital.shared.exception;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException() {
        super("Refresh token inválido ou expirado");
    }
}