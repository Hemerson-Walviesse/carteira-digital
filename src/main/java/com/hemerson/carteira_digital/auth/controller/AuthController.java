package com.hemerson.carteira_digital.auth.controller;

import com.hemerson.carteira_digital.auth.dto.CadastroRequest;
import com.hemerson.carteira_digital.auth.dto.LoginRequest;
import com.hemerson.carteira_digital.auth.dto.TokenResponse;
import com.hemerson.carteira_digital.auth.dto.UsuarioResponse;
import com.hemerson.carteira_digital.auth.service.AuthService;
import com.hemerson.carteira_digital.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody @Valid CadastroRequest request) {
        return usuarioService.cadastrar(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal Jwt jwt) {
        return usuarioService.buscarPorId(UUID.fromString(jwt.getSubject()));
    }
}