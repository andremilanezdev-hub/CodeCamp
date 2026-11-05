package com.example.codecamp.repository;

import com.example.codecamp.entities.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    Optional<Certificado> findByEstudante_IdAndCurso_Id(Long estudanteId, Long cursoId);
}
