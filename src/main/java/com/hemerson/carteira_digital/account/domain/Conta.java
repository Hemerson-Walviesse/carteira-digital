package com.hemerson.carteira_digital.account.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conta")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false, length = 4)
    private String agencia = "0001";

    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status = StatusConta.ATIVA;

    @Version
    private Long version;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public Conta(UUID usuarioId, String numero) {
        this.usuarioId = usuarioId;
        this.numero = numero;
    }

    @PrePersist
    void prePersist() {
        this.criadoEm = OffsetDateTime.now();
    }
}