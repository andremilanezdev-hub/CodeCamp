package com.example.codecamp.repository;


import com.example.codecamp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    boolean existsUsuarioByCpfAndSenha(String cpf, String senha);
    //Retorna um registro
    Optional<Usuario> getUsuarioByCpf(String cpf);
    //Retorna vários registros
    Optional<List<Usuario>> getUsuariosByStatus(String status);
}
