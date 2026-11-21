package com.hemerson.carteira_digital.auth.service;

import com.hemerson.carteira_digital.auth.domain.Usuario;
import com.hemerson.carteira_digital.auth.dto.LoginRequest;
import com.hemerson.carteira_digital.auth.dto.RefreshRequest;
import com.hemerson.carteira_digital.auth.dto.TokenResponse;
import com.hemerson.carteira_digital.auth.repository.UsuarioRepository;
import com.hemerson.carteira_digital.shared.exception.CredenciaisInvalidasException;
import com.hemerson.carteira_digital.shared.exception.TokenInvalidoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       RefreshTokenService refreshTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.hashFalso = passwordEncoder.encode("usuario-inexistente");
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        String hash = usuario.map(Usuario::getSenha).orElse(hashFalso);
        boolean senhaCorreta = passwordEncoder.matches(request.senha(), hash);

        if (usuario.isEmpty() || !senhaCorreta) {
            throw new CredenciaisInvalidasException();
        }
        Usuario u = usuario.get();
        return montarResposta(u, refreshTokenService.criar(u.getId()));
    }

    @Transactional(noRollbackFor = TokenInvalidoException.class)
    public TokenResponse refresh(RefreshRequest request) {
        RefreshTokenService.Rotacao rotacao = refreshTokenService.rotacionar(request.refreshToken());
        Usuario usuario = usuarioRepository.findById(rotacao.usuarioId())
                .orElseThrow(TokenInvalidoException::new);
        return montarResposta(usuario, rotacao.novoRefreshToken());
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenService.revogar(request.refreshToken());
    }

    private TokenResponse montarResposta(Usuario usuario, String refreshToken) {
        return new TokenResponse(
                tokenService.gerarAccessToken(usuario),
                refreshToken,
                "Bearer",
                tokenService.expiracaoEmSegundos());
    }
}