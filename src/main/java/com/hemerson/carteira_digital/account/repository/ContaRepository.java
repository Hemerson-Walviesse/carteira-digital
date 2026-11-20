package com.hemerson.carteira_digital.account.repository;

import com.hemerson.carteira_digital.account.domain.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {
    List<Conta> findByUsuarioId(UUID usuarioId);
    boolean existsByNumero(String numero);
}