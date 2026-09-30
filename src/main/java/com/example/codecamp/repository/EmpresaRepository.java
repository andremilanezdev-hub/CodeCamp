package com.example.codecamp.repository;


import com.example.codecamp.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa,Long> {

    //Consulta empresa por cnpj
    Optional<Empresa> getEmpresaByCnpj(String cnpj);
}
