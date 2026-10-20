package com.example.codecamp.repository;


import com.example.codecamp.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Fornece operações de persistência e consulta para a entidade Empresa.
public interface EmpresaRepository extends JpaRepository<Empresa,Long> {

    // Busca uma empresa pelo CNPJ; Optional fica vazio quando não há correspondência.
    Optional<Empresa> getEmpresaByCnpj(String cnpj);
}
