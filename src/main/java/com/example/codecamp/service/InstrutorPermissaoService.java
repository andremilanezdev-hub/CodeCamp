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

/**
 * Centraliza as verificações de identidade e propriedade usadas por cursos, módulos e matrículas.
 * A permissão é calculada a partir do vínculo persistido no banco e do CPF do JWT validado pelo filtro;
 * assim, o ID informado no corpo da requisição nunca é tratado isoladamente como prova de identidade.
 */
@Service
public class InstrutorPermissaoService {

    private final UsuarioRepository usuarioRepository;

    public InstrutorPermissaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Retorna o usuário que atua como instrutor somente quando o ID solicitado corresponde
     * ao CPF extraído do token. A consulta é somente leitura, por isso a transação é marcada readOnly.
     */
    @Transactional(readOnly = true)
    public Usuario buscarInstrutor(Long usuarioId, String cpfAutenticado) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o usuario_id do instrutor");
        }
        if (cpfAutenticado == null || cpfAutenticado.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sem usuário associado");
        }

        // Recupera o registro indicado pelo cliente para conferir sua existência e estado persistido.
        Usuario instrutor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrutor não encontrado"));

        // O CPF vem do subject do JWT validado, não do corpo enviado pelo cliente.
        Usuario usuarioDoToken = usuarioRepository.getUsuarioByCpf(cpfAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário do token não encontrado"));

        // Comparar chaves primárias evita depender de igualdade por referência entre entidades JPA.
        if (!Objects.equals(instrutor.getId(), usuarioDoToken.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: usuario_id não corresponde ao usuário autenticado");
        }
        return instrutor;
    }

    /**
     * Aplica a regra de negócio de propriedade do curso: somente o instrutor que o cadastrou
     * pode realizar operações que dependem dessa propriedade, inclusive matricular estudantes.
     */
    @Transactional(readOnly = true)
    public void validarPropriedadeDoCurso(Curso curso, Long usuarioId, String cpfAutenticado) {
        Usuario instrutor = buscarInstrutor(usuarioId, cpfAutenticado);
        if (curso.getUsuarioCadastro() == null
                || !Objects.equals(curso.getUsuarioCadastro().getId(), instrutor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: o curso pertence a outro instrutor");
        }
    }

    /**
     * Mantém a mesma regra de propriedade para módulos, que devem pertencer ao instrutor responsável
     * tanto pelo próprio módulo quanto pelo curso ao qual estão vinculados.
     */
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
