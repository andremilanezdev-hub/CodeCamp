package com.example.codecamp.repository;

import com.example.codecamp.entities.ConclusaoModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConclusaoModuloRepository extends JpaRepository<ConclusaoModulo, Long> {

    Optional<ConclusaoModulo> findByEstudante_IdAndModulo_Id(Long estudanteId, Long moduloId);

    long countByEstudante_IdAndModulo_Curso_IdAndConcluidoTrue(Long estudanteId, Long cursoId);
}
