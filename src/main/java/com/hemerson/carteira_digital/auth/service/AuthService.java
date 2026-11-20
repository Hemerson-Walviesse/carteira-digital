package com.hemerson.carteira_digital.auth.service;

import com.hemerson.carteira_digital.auth.domain.Usuario;
import com.hemerson.carteira_digital.auth.dto.LoginRequest;
import com.hemerson.carteira_digital.auth.dto.TokenResponse;
import com.hemerson.carteira_digital.auth.repository.UsuarioRepository;
import com.hemerson.carteira_digital.shared.exception.CredenciaisInvalidasException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.hashFalso = passwordEncoder.encode("usuario-inexistente");
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        // sempre roda o BCrypt, mesmo se o usuário não existir
        String hash = usuario.map(Usuario::getSenha).orElse(hashFalso);
        boolean senhaCorreta = passwordEncoder.matches(request.senha(), hash);

        if (usuario.isEmpty() || !senhaCorreta) {
            throw new CredenciaisInvalidasException();
        }
        return tokenService.gerar(usuario.get());
    }
}