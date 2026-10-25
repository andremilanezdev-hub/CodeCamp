package com.example.codecamp.repository;


import com.example.codecamp.entities.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Oferece operações de persistência e consulta para a entidade Estudante.
public interface EstudanteRepository extends JpaRepository<Estudante,Long> {

    /**
     * Consulta se a tabela de junção já contém o par estudante-curso.
     * O método derivado mantém a regra de persistência no repositório existente da entidade proprietária,
     * sem criar um MatriculaRepository para uma associação N:M que não possui entidade própria.
     */
    boolean existsByIdAndCursos_Id(Long estudanteId, Long cursoId);

    // Busca um estudante pelo ID; retorna Optional vazio quando não há correspondência.
    Optional<Estudante> getEstudanteById(Long id);

    // Busca um estudante pelo CPF.
    Optional<Estudante> getEstudanteByCpf(Long cpf);

    // Busca estudantes pela situação informada, como ativo ou desativado.
    Optional<List<Estudante>> getUsuariosByStatus(String status);
}
