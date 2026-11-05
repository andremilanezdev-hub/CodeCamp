package com.example.codecamp.service;

import com.example.codecamp.DTO.ConcluirModuloRequest;
import com.example.codecamp.DTO.ConclusaoModuloResponse;
import com.example.codecamp.entities.*;
import com.example.codecamp.repository.CertificadoRepository;
import com.example.codecamp.repository.ConclusaoModuloRepository;
import com.example.codecamp.repository.EstudanteRepository;
import com.example.codecamp.repository.ModuloRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

/** Registra conclusão de módulos e emite no máximo um certificado por estudante e curso. */
@Service
public class ConclusaoModuloService {

    private final EstudanteRepository estudanteRepository;
    private final ModuloRepository moduloRepository;
    private final ConclusaoModuloRepository conclusaoModuloRepository;
    private final CertificadoRepository certificadoRepository;

    public ConclusaoModuloService(EstudanteRepository estudanteRepository,
                                  ModuloRepository moduloRepository,
                                  ConclusaoModuloRepository conclusaoModuloRepository,
                                  CertificadoRepository certificadoRepository) {
        this.estudanteRepository = estudanteRepository;
        this.moduloRepository = moduloRepository;
        this.conclusaoModuloRepository = conclusaoModuloRepository;
        this.certificadoRepository = certificadoRepository;
    }

    public ConclusaoModuloResponse concluirModulo(ConcluirModuloRequest request) {
        Estudante estudante = estudanteRepository.findById(request.getEstudante_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estudante não encontrado"));
        Modulo modulo = moduloRepository.findById(request.getModulo_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Módulo não encontrado"));

        Curso curso = modulo.getCurso();
        Long cursoId = curso.getId();
        if (!estudanteRepository.existsByIdAndCursos_Id(estudante.getId(), cursoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O estudante não está matriculado no curso deste módulo");
        }

        registrarConclusao(estudante, modulo);
        long totalModulos = moduloRepository.countByCurso_Id(cursoId);
        long modulosConcluidos = conclusaoModuloRepository
                .countByEstudante_IdAndModulo_Curso_IdAndConcluidoTrue(estudante.getId(), cursoId);
        boolean cursoConcluido = totalModulos > 0 && modulosConcluidos >= totalModulos;

        Certificado certificado = null;
        if (cursoConcluido) {
            certificado = obterOuEmitirCertificado(estudante, curso);
        }

        String mensagem = certificado == null
                ? "Módulo marcado como concluído"
                : "Curso concluído; certificado disponível";
        return new ConclusaoModuloResponse(
                estudante.getId(),
                modulo.getId(),
                cursoId,
                cursoConcluido,
                certificado == null ? null : certificado.getCodigoValidacao(),
                mensagem
        );
    }

    private ConclusaoModulo registrarConclusao(Estudante estudante, Modulo modulo) {
        return conclusaoModuloRepository.findByEstudante_IdAndModulo_Id(estudante.getId(), modulo.getId())
                .orElseGet(() -> {
                    ConclusaoModulo novaConclusao = new ConclusaoModulo(
                            estudante, modulo, LocalDateTime.now());
                    try {
                        return conclusaoModuloRepository.save(novaConclusao);
                    } catch (DataIntegrityViolationException excecao) {
                        // A restrição única estudante+módulo cobre duas marcações simultâneas.
                        return conclusaoModuloRepository
                                .findByEstudante_IdAndModulo_Id(estudante.getId(), modulo.getId())
                                .orElseThrow(() -> excecao);
                    }
                });
    }

    private Certificado obterOuEmitirCertificado(Estudante estudante, Curso curso) {
        return certificadoRepository.findByEstudante_IdAndCurso_Id(estudante.getId(), curso.getId())
                .orElseGet(() -> {
                    Certificado novoCertificado = new Certificado(
                            estudante,
                            curso,
                            UUID.randomUUID().toString(),
                            LocalDateTime.now()
                    );
                    try {
                        return certificadoRepository.save(novoCertificado);
                    } catch (DataIntegrityViolationException excecao) {
                        // A restrição única estudante+curso impede certificados duplicados em concorrência.
                        return certificadoRepository
                                .findByEstudante_IdAndCurso_Id(estudante.getId(), curso.getId())
                                .orElseThrow(() -> excecao);
                    }
                });
    }
}
