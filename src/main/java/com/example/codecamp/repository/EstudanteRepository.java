package com.example.codecamp.repository;


import com.example.codecamp.entities.Estudante;
import com.example.codecamp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante,Long> {

    //Retorna um registro
    Optional<Estudante> getEstudanteById(Long id);
    Optional<Estudante> getEstudanteByCpf(Long cpf);
    //Retorna vários registros
    Optional<List<Estudante>> getUsuariosByStatus(String status);
}
