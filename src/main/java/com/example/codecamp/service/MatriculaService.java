package com.example.codecamp.service;

import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Estudante;
import com.example.codecamp.repository.CursoRepository;
import com.example.codecamp.repository.EstudanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// Realiza a matrícula do estudante depois de validar a propriedade do curso.
@Service
public class MatriculaService {

    private final CursoRepository cursoRepository;
    private final EstudanteRepository estudanteRepository;
    private final InstrutorPermissaoService instrutorPermissaoService;

    public MatriculaService(CursoRepository cursoRepository,
                            EstudanteRepository estudanteRepository,
                            InstrutorPermissaoService instrutorPermissaoService) {
        this.cursoRepository = cursoRepository;
        this.estudanteRepository = estudanteRepository;
        this.instrutorPermissaoService = instrutorPermissaoService;
    }

    // Associa um estudante ao curso somente se o instrutor informado for o criador do curso.
    @Transactional
    public Curso matricular(Long estudanteId, Long cursoId, Long usuarioId, String cpfAutenticado) {
        if (estudanteId == null || cursoId == null || usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe estudante_id, curso_id e usuario_id");
        }

        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado"));
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));

        instrutorPermissaoService.validarPropriedadeDoCurso(curso, usuarioId, cpfAutenticado);

        if (estudante.getCursos().contains(curso)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudante já matriculado no curso");
        }

        curso.adicionarEstudante(estudante);
        estudanteRepository.save(estudante);
        return curso;
    }
}
