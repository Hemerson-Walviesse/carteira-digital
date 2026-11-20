package com.hemerson.carteira_digital.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String cpfMascarado,
        OffsetDateTime criadoEm
) {}