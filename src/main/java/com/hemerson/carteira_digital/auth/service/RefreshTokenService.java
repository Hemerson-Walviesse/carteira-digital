package com.hemerson.carteira_digital.auth.service;

import com.hemerson.carteira_digital.auth.domain.RefreshToken;
import com.hemerson.carteira_digital.auth.repository.RefreshTokenRepository;
import com.hemerson.carteira_digital.shared.exception.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final long expiracaoDias;

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${jwt.refresh-expiracao-dias}") long expiracaoDias) {
        this.repository = repository;
        this.expiracaoDias = expiracaoDias;
    }

    public String criar(UUID usuarioId) {
        return gerarNovo(usuarioId).tokenPuro();
    }

    public Rotacao rotacionar(String tokenPuro) {
        RefreshToken atual = repository.buscarParaAtualizar(hash(tokenPuro))
                .orElseThrow(TokenInvalidoException::new);

        if (atual.estaRevogado()) {
            // token já usado sendo reapresentado = possível roubo
            repository.revogarTodosDoUsuario(atual.getUsuarioId(), OffsetDateTime.now());
            throw new TokenInvalidoException();
        }
        if (atual.estaExpirado()) {
            throw new TokenInvalidoException();
        }

        Gerado novo = gerarNovo(atual.getUsuarioId());
        atual.substituirPor(novo.entidade().getId());
        return new Rotacao(atual.getUsuarioId(), novo.tokenPuro());
    }

    public void revogar(String tokenPuro) {
        repository.findByTokenHash(hash(tokenPuro)).ifPresent(RefreshToken::revogar);
    }

    private Gerado gerarNovo(UUID usuarioId) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String tokenPuro = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken entidade = new RefreshToken(usuarioId, hash(tokenPuro),
                OffsetDateTime.now().plusDays(expiracaoDias));
        repository.save(entidade);
        return new Gerado(entidade, tokenPuro);
    }

    static String hash(String valor) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha256.digest(valor.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public record Rotacao(UUID usuarioId, String novoRefreshToken) {}

    private record Gerado(RefreshToken entidade, String tokenPuro) {}
}