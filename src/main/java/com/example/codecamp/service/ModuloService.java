package com.example.codecamp.service;

import com.example.codecamp.DTO.ModuloConsultaResponse;
import com.example.codecamp.DTO.ModuloRequest;
import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Modulo;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.ModuloRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Centraliza as operações que dependem do vínculo Curso-Módulo.
 * O serviço mantém validações de cardinalidade, propriedade do instrutor e conversão para DTO
 * dentro de uma transação, preservando a separação já usada entre controller e repositórios.
 */
@Service
public class ModuloService {

    private final ModuloRepository moduloRepository;
    private final CursoRepository cursoRepository;
    private final InstrutorPermissaoService instrutorPermissaoService;

    public ModuloService(ModuloRepository moduloRepository,
                         CursoRepository cursoRepository,
                         InstrutorPermissaoService instrutorPermissaoService) {
        this.moduloRepository = moduloRepository;
        this.cursoRepository = cursoRepository;
        this.instrutorPermissaoService = instrutorPermissaoService;
    }

    /** Lista todos os módulos em DTOs para evitar expor diretamente associações JPA. */
    @Transactional(readOnly = true)
    public List<ModuloConsultaResponse> listarTodos() {
        return moduloRepository.findAll().stream()
                .map(ModuloConsultaResponse::new)
                .toList();
    }

    /** Busca um módulo e converte seu curso para IDs/campos simples dentro da transação. */
    @Transactional(readOnly = true)
    public Optional<ModuloConsultaResponse> buscarPorId(Long moduloId) {
        return moduloRepository.findById(moduloId).map(ModuloConsultaResponse::new);
    }

    /**
     * Retorna os módulos filhos de um curso existente. A chave estrangeira reside no módulo,
     * por isso o repositório filtra por Modulo.curso.id sem manter uma segunda tabela de vínculo.
     */
    @Transactional(readOnly = true)
    public List<ModuloConsultaResponse> listarPorCurso(Long cursoId) {
        validarId(cursoId, "curso_id");
        if (!cursoRepository.existsById(cursoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado");
        }
        return moduloRepository.findAllByCurso_Id(cursoId).stream()
                .map(ModuloConsultaResponse::new)
                .toList();
    }

    /**
     * Cria módulo como filho de um curso existente. O curso vem da rota hierárquica ou do DTO
     * legado e é sempre validado no serviço; a permissão compara o instrutor do JWT ao criador do curso.
     */
    @Transactional
    public Modulo cadastrar(Long cursoId, ModuloRequest request, String cpfAutenticado) {
        validarId(cursoId, "curso_id");
        if (request == null || request.getUsuario_id() == null
                || request.getUsuario_id() <= 0
                || request.getNome() == null || request.getNome().isBlank()
                || request.getCargahoraria() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe instrutor, nome e carga horária válida para o módulo");
        }
        if (request.getCurso_id() != null && !request.getCurso_id().equals(cursoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "curso_id do corpo deve corresponder ao curso da rota");
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso não encontrado"));

        // Impede que um instrutor inclua módulos em curso de outra pessoa.
        instrutorPermissaoService.validarPropriedadeDoCurso(
                curso, request.getUsuario_id(), cpfAutenticado);

        Modulo modulo = new Modulo();
        modulo.setNome(request.getNome().trim());
        modulo.setCargahoraria((long) request.getCargahoraria());
        // O setter mantém também Curso.modulos em memória; Modulo.curso persiste curso_id.
        curso.adicionarModulo(modulo);
        modulo.setUsuarioCadastro(instrutorPermissaoService.buscarInstrutor(
                request.getUsuario_id(), cpfAutenticado));
        modulo.setDataCadastro(LocalDateTime.now());
        modulo.setStatus("A");

        return moduloRepository.save(modulo);
    }

    private void validarId(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe um " + campo + " válido");
        }
    }
}
