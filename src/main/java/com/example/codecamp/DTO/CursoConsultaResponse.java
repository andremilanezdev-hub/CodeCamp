package com.example.codecamp.DTO;

import com.example.codecamp.entities.Curso;
import com.example.codecamp.entities.Usuario;

import java.util.List;

public class CursoConsultaResponse {

    public CursoConsultaResponse() {
    }

    public CursoConsultaResponse(Curso curso) {
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.cargahoraria = curso.getCargahoraria();
        this.usuario = curso.getAlunos().stream().map(UsuarioConsultaResponse::new).toList(); //Logica Nova Curso consulta Resposta
    }

    private Long id;
    private String nome;
    private long cargahoraria;
    private List<UsuarioConsultaResponse> usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getCargahoraria() {
        return cargahoraria;
    }

    public void setCargahoraria(long cargahoraria) {
        this.cargahoraria = cargahoraria;
    }

    public List<UsuarioConsultaResponse> getUsuario() {
        return usuario;
    }

    public void setUsuario(List<UsuarioConsultaResponse> usuario) {
        this.usuario = usuario;
    }
}
