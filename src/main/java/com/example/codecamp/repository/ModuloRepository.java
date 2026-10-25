package com.example.codecamp.repository;


import com.example.codecamp.entities.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo,Long> {

    /**
     * Busca somente os módulos associados ao curso informado, navegando pela propriedade
     * Modulo.curso sem montar SQL manual; Spring Data traduz o método em consulta JPA.
     */
    List<Modulo> findAllByCurso_Id(Long cursoId);

    //Retorna um registro
    Optional<Modulo> getModuloByNome(String nome);
    Optional<Modulo> getMOduloById(Long id);
    //Retorna vários registros
    Optional<List<Modulo>> getModuloByStatus(String status);
}
