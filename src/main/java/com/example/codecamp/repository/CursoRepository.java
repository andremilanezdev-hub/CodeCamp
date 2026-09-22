package com.example.codecamp.repository;


import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso,Long> {

    boolean existsCursoById(Long id);
    Optional<List<Curso>> getCursoByNomeContaining(String nome);
    Optional<List<Curso>> getCursoByStatus(String status);
}
