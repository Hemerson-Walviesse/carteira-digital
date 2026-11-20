package com.hemerson.carteira_digital.auth.controller;

import com.hemerson.carteira_digital.auth.dto.CadastroRequest;
import com.hemerson.carteira_digital.auth.dto.UsuarioResponse;
import com.hemerson.carteira_digital.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody @Valid CadastroRequest request) {
        return usuarioService.cadastrar(request);
    }
}