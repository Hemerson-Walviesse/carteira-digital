package com.hemerson.carteira_digital.shared.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: campos inválidos no JSON
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacao(MethodArgumentNotValidException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos");
        problema.setTitle("Dados inválidos");

        Map<String, String> campos = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        erro -> erro.getField(),
                        erro -> erro.getDefaultMessage(),
                        (primeira, segunda) -> primeira));
        problema.setProperty("campos", campos);
        return problema;
    }

    // 409: e-mail ou CPF já cadastrado
    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail conflito(ConflitoException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("Conflito");
        return problema;
    }

    // 409: o banco barrou (ex.: dois cadastros iguais ao mesmo tempo)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridade(DataIntegrityViolationException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Os dados conflitam com um registro existente");
        problema.setTitle("Conflito");
        return problema;
    }

    // 401: login falhou
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail credenciais(CredenciaisInvalidasException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problema.setTitle("Não autorizado");
        return problema;
    }

    // 404: recurso não existe
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Não encontrado");
        return problema;
    }

}