package com.hemerson.carteira_digital.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false,unique = true,length = 11)
    private String cpf;

    @Column(nullable = false,unique = true, length = 160)
    private String email;

    @Column(name = "senha_hash",nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Papel papel = Papel.CLIENTE;

    @Column(name = "criado_em", nullable = false,updatable = false)
    private OffsetDateTime criadoEm;

    public Usuario(String nome, String cpf, String email, String senha){
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    @PrePersist
    void prePersist(){
        this.criadoEm = OffsetDateTime.now();
    }
}
