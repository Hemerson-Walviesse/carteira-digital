package com.hemerson.carteira_digital.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tipo,
        long expiraEmSegundos
) {}