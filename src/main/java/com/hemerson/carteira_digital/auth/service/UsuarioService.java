package com.hemerson.carteira_digital.auth.service;

import com.hemerson.carteira_digital.auth.domain.Usuario;
import com.hemerson.carteira_digital.auth.dto.CadastroRequest;
import com.hemerson.carteira_digital.auth.dto.UsuarioResponse;
import com.hemerson.carteira_digital.auth.repository.UsuarioRepository;
import com.hemerson.carteira_digital.shared.exception.ConflitoException;
import com.hemerson.carteira_digital.shared.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse cadastrar(CadastroRequest request) {
        String cpf = request.cpf().replaceAll("\\D", "");
        String email = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ConflitoException("CPF já cadastrado");
        }

        String senhaHash = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(request.nome().trim(), cpf, email, senhaHash);
        usuarioRepository.save(usuario);

        return paraResponse(usuario);
    }

    private UsuarioResponse paraResponse(Usuario usuario) {
        String cpf = usuario.getCpf();
        String mascarado = "***." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-**";
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                mascarado, usuario.getCriadoEm());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        return paraResponse(usuario);
    }
}