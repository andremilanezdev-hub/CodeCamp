package com.example.codecamp.service;

import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.entities.Usuario;
import com.example.codecamp.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

// Valida o ID de instrutor enviado pela requisição contra os vínculos persistidos no banco.
@Service
public class InstrutorPermissaoService {

    private final UsuarioRepository usuarioRepository;

    public InstrutorPermissaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Confere se o usuario_id informado pertence ao CPF autenticado pelo filtro JWT.
    @Transactional(readOnly = true)
    public Usuario buscarInstrutor(Long usuarioId, String cpfAutenticado) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o usuario_id do instrutor");
        }
        Usuario instrutor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrutor não encontrado"));

        Usuario usuarioDoToken = usuarioRepository.getUsuarioByCpf(cpfAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário do token não encontrado"));
        if (!Objects.equals(instrutor.getId(), usuarioDoToken.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: usuario_id não corresponde ao usuário autenticado");
        }
        return instrutor;
    }

    // Só permite operar no curso quando o ID recebido corresponde ao seu criador.
    @Transactional(readOnly = true)
    public void validarPropriedadeDoCurso(Curso curso, Long usuarioId, String cpfAutenticado) {
        Usuario instrutor = buscarInstrutor(usuarioId, cpfAutenticado);
        if (curso.getUsuarioCadastro() == null
                || !Objects.equals(curso.getUsuarioCadastro().getId(), instrutor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: o curso pertence a outro instrutor");
        }
    }

    // Só permite alterar o módulo quando ele e seu curso pertencem ao instrutor informado.
    @Transactional(readOnly = true)
    public void validarPropriedadeDoModulo(Modulo modulo, Long usuarioId, String cpfAutenticado) {
        Usuario instrutor = buscarInstrutor(usuarioId, cpfAutenticado);
        boolean moduloDoInstrutor = modulo.getUsuarioCadastro() != null
                && Objects.equals(modulo.getUsuarioCadastro().getId(), instrutor.getId());
        boolean cursoDoInstrutor = modulo.getCurso() != null
                && modulo.getCurso().getUsuarioCadastro() != null
                && Objects.equals(modulo.getCurso().getUsuarioCadastro().getId(), instrutor.getId());

        if (!moduloDoInstrutor || !cursoDoInstrutor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: o módulo ou seu curso pertence a outro instrutor");
        }
    }
}
